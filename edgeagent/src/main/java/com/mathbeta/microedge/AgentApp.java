package com.mathbeta.microedge;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mathbeta.alphaboot.entity.Result;
import com.mathbeta.microedge.config.Constants;
import com.mathbeta.microedge.entity.*;
import com.mathbeta.microedge.utils.AppManager;
import com.mathbeta.microedge.utils.ConfigHelper;
import com.mathbeta.microedge.utils.DockerUtil;
import com.mathbeta.microedge.utils.NetworkUtil;
import com.mathbeta.microedge.ws.WebSocketClientManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * agent app
 *
 * @author xuxiuyou
 * @date 2023/9/7 17:35
 */
@Slf4j
public class AgentApp {
    /**
     * 业务处理线程池
     */
    public static final ScheduledExecutorService workers = Executors.newScheduledThreadPool(64);
    public static final HttpClient httpClient = HttpClients.createDefault();

    public static void main(String[] args) {
        AgentConfig config = tryRegister();
        if (null == config) {
            throw new RuntimeException("Unable to load agent config or" +
                    " register agent to cloud master, please check again!");
        }
        loadAndMonitorApps();

        try {
            WebSocketClientManager.getManager().init(config);
            heartbeat(config);
        } catch (Exception e) {
            log.error("Failed to start websocket client", e);
        }
    }

    /**
     * 加载并开始监控应用状态（应用容器异常时进行恢复）
     */
    private static void loadAndMonitorApps() {
        File file = new File(String.format("%s/.microedge-agent/apps.yaml", System.getProperty("user.home")));
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                log.error("Failed to create app instance store file {}", file, e);
            }
        }
        Yaml yaml = new Yaml();
        try (InputStream is = new FileInputStream(file)) {
            AppInstances appInstances = yaml.loadAs(is, AppInstances.class);
            if (null != appInstances && CollectionUtils.isNotEmpty(appInstances.getInstances())) {
                appInstances.getInstances().forEach(instance -> {
                    AppManager.getManager().addInstance(instance.getContainerName(), instance);
                });
            }
        } catch (IOException e) {
            log.error("Failed to create inputstream from file {}", file, e);
        }

        workers.scheduleWithFixedDelay(() -> {
            try {
                AppManager.getManager().listInstances().forEach(instance -> {
                    Optional<String> status = DockerUtil.status(instance.getContainerId());
                    if (status.isEmpty()) {
                        // 再次检查应用是否被删除，未被删除才需要重新创建容器
                        if (AppManager.getManager().checkExistence(instance.getContainerName())) {
                            log.warn("Container {} not exists, redeploying app {}", instance.getContainerId(), instance.getAppName());
                            AppManager.getManager().removeInstance(instance.getContainerName());

                            String containerName = instance.getContainerName();
                            String id = DockerUtil.run(instance.getImage(),
                                    containerName,
                                    instance.getEnv(),
                                    instance.getPortMappings(),
                                    instance.getVolumeMappings());

                            instance.setContainerId(id);
                            AppManager.getManager().addInstance(containerName, instance);
                        }
                    } else if (!Objects.equals(status.get(), "running")) {
                        log.warn("Container {} is not running, trying to restart it", instance.getContainerId());
                        DockerUtil.restartContainer(instance.getContainerId());
                    }
                });

                try (Writer writer = new FileWriter(file)) {
                    AppInstances appInstances = AppInstances.builder()
                            .instances(AppManager.getManager().listInstances())
                            .build();
                    writer.write(yaml.dumpAsMap(appInstances));
                } catch (IOException e) {
                    log.error("Failed to create writer from file {}", file, e);
                }
            } catch (Exception e) {
                log.error("Failed to monitor app containers", e);
            }
        }, 1000L, 3000L, TimeUnit.MILLISECONDS);
    }

    /**
     * 尝试注册到master
     *
     * @return
     */
    private static AgentConfig tryRegister() {
        Yaml yaml = new Yaml();
        File file = new File(String.format("%s/.microedge-agent/config.yaml", System.getProperty("user.home")));
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
                doRegister(yaml, file);
            } catch (IOException e) {
                log.error("Failed to create config file {}", file, e);
            }
        }
        try (InputStream is = new FileInputStream(file)) {
            return yaml.loadAs(is, AgentConfig.class);
        } catch (IOException e) {
            log.error("Failed to create inputstream from file {}", file, e);
        }

        return null;
    }

    /**
     * 实际注册动作
     *
     * @param yaml
     * @param file
     */
    private static void doRegister(Yaml yaml, File file) {
        HttpPost post = new HttpPost(String.format("%s%s",
                ConfigHelper.getInstance().get("master.api.address"),
                ConfigHelper.getInstance().get("master.api.registerUrl")));
        post.setHeader(Constants.CONTENT_TYPE_HEADER, Constants.JSON_CONTENT_TYPE);
        try {
            String ip = NetworkUtil.getHostAddress();
            String namespace = (String) ConfigHelper.getInstance().get("agent.namespace");
            post.setEntity(new StringEntity(JSON.toJSONString(RegisterMsg.builder()
                    .ip(ip)
                    .namespace(namespace)
                    .build())));
            HttpResponse resp = httpClient.execute(post);
            if (2 == resp.getStatusLine().getStatusCode() / 100) {
                String content = IOUtils.toString(resp.getEntity().getContent());
                if (null != content && !content.isEmpty()) {
                    Result result = JSON.parseObject(content, Result.class);
                    if (result.isSuccess()) {
                        JSONObject jo = (JSONObject) result.getContent();
                        AgentConfig config = AgentConfig.builder()
                                .agent(AgentConfig.InnerConfig.builder()
                                        .id(jo.getString("id"))
                                        .token(jo.getString("token"))
                                        .ip(ip)
                                        .registerTime(new Date())
                                        .build())
                                .build();
                        try (Writer writer = new FileWriter(file)) {
                            writer.write(yaml.dumpAsMap(config));
                        }
                    } else {
                        log.error("Failed to register agent: {}", JSON.toJSONString(result));
                    }
                }
            } else {
                log.error("Failed to request master to register agent, response is {}", resp.toString());
            }
        } catch (IOException e) {
            log.error("Failed to register agent", e);
        } finally {
            post.releaseConnection();
        }
    }

    /**
     * 启动定时心跳
     *
     * @param config
     */
    private static void heartbeat(AgentConfig config) {
        workers.scheduleWithFixedDelay(() -> {
            try {
                WebSocketClientManager.getManager().getClient().send(JSON.toJSONString(HeartbeatMsg.builder()
                        .type(BaseMsg.TYPE_HEARTBEAT)
                        .token(config.getAgent().getToken())
                        .agentId(config.getAgent().getId())
                        .appInstances(AppManager.getManager().listInstances())
                        .build()));
            } catch (Exception e) {
                log.error("Failed to send heartbeat message to master", e);
                WebSocketClientManager.getManager().reconnect();
            }
        }, 1000L, 10000L, TimeUnit.MILLISECONDS);
    }
}
