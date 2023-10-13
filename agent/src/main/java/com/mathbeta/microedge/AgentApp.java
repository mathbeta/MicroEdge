package com.mathbeta.microedge;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mathbeta.alphaboot.entity.Result;
import com.mathbeta.microedge.config.Constants;
import com.mathbeta.microedge.entity.*;
import com.mathbeta.microedge.utils.*;
import com.mathbeta.microedge.ws.WebSocketClientManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

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
        if (null == args || args.length == 0) {
            startAgent();
        } else {
            switch (args[0]) {
                case "config":
                    configAgent(args);
                    break;

                default:
                    log.error("Unknown command {} with args {}", args[0], String.join(" ", args));
                    break;
            }
        }
    }

    /**
     * 配置Agent
     *
     * @param args
     */
    private static void configAgent(String[] args) {
        int len = args.length - 1;
        if (len > 0) {
            String namespace = "default";
            String apiAddress = "http://127.0.0.1:8090/master";
            String websocketAddress = "ws://127.0.0.1:8090/master/ws";
            for (int i = 1; i < len; i++) {
                if (Objects.equals("--namespace", args[i])) {
                    i++;
                    namespace = args[i];
                }
                if (Objects.equals("--master-api-address", args[i])) {
                    i++;
                    apiAddress = args[i];
                }
                if (Objects.equals("--master-websocket-address", args[i])) {
                    i++;
                    websocketAddress = args[i];
                }
            }

            configAgent(namespace, apiAddress, websocketAddress);
        }
    }

    private static void configAgent(String namespace, String apiAddress, String websocketAddress) {
        File configFile = new File(Constants.DEFAULT_CONFIG_FILE);
        if (!configFile.exists()) {
            log.info("No config file found {}, creating default config file", Constants.DEFAULT_CONFIG_FILE);
            try {
                configFile.createNewFile();
                try (InputStream is = ConfigHelper.class.getResourceAsStream("/config.yaml");
                     OutputStream os = new FileOutputStream(configFile)) {
                    IOUtils.copy(is, os);
                } catch (IOException e) {
                    log.error("Failed to read built-in config.yaml", e);
                }
            } catch (IOException e) {
                log.error(String.format("Failed to create config file %s", Constants.DEFAULT_CONFIG_FILE), e);
            }
        }
        // agent配置文件需要从最终打包jar外部读取，不能读取jar内部的配置文件，不便于修改配置参数
        try (InputStream is = new FileInputStream(configFile)) {
            AgentConfig agentConfig = YamlUtil.loadAs(is, AgentConfig.class);
            agentConfig.getMaster().setApiAddress(apiAddress);
            agentConfig.getMaster().setWebsocketAddress(websocketAddress);

            log.info("Registering agent to master {}", apiAddress);
            AgentConfig.AgentInfo agentInfo = doRegister(apiAddress, agentConfig.getMaster().getRegisterUrl(), namespace);
            agentInfo.setNamespace(namespace);
            agentConfig.setAgent(agentInfo);

            // 将agent配置写入配置文件
            try (Writer writer = new FileWriter(Constants.DEFAULT_CONFIG_FILE)) {
                writer.write(YamlUtil.dumpAsMap(agentConfig));
            } catch (IOException e) {
                log.error("Failed to create writer from file {}", Constants.DEFAULT_CONFIG_FILE, e);
            }
        } catch (IOException e) {
            log.error("Failed to read config file {}", Constants.DEFAULT_CONFIG_FILE, e);
        }
    }

    /**
     * 实际注册动作
     *
     * @param apiAddress
     * @param registerUrl
     * @param namespace
     * @return
     */
    private static AgentConfig.AgentInfo doRegister(String apiAddress,
                                                    String registerUrl,
                                                    String namespace) {
        HttpPost post = new HttpPost(String.format("%s%s", apiAddress, registerUrl));
        post.setHeader(Constants.CONTENT_TYPE_HEADER, Constants.JSON_CONTENT_TYPE);
        try {
            String ip = NetworkUtil.getHostAddress();
            post.setEntity(new StringEntity(RegisterMsg.builder()
                    .type(BaseMsg.TYPE_REGISTER)
                    .ip(ip)
                    .namespace(namespace)
                    .build().toJsonString()));
            HttpResponse resp = httpClient.execute(post);
            if (2 == resp.getStatusLine().getStatusCode() / 100) {
                String content = IOUtils.toString(resp.getEntity().getContent());
                if (null != content && !content.isEmpty()) {
                    Result result = JSON.parseObject(content, Result.class);
                    if (result.isSuccess()) {
                        JSONObject jo = (JSONObject) result.getContent();
                        return AgentConfig.AgentInfo.builder()
                                .id(jo.getString("id"))
                                .token(jo.getString("token"))
                                .ip(ip)
                                .registerTime(new Date())
                                .build();
                    } else {
                        log.error("Failed to register agent: {}", content);
                    }
                }
            } else {
                log.error("Failed to request master to register agent, response is {}", resp);
            }
        } catch (IOException e) {
            log.error("Failed to register agent", e);
        } finally {
            post.releaseConnection();
        }
        return null;
    }

    /**
     * 启动Agent
     */
    public static void startAgent() {
        AgentConfig config = loadAgentConfig();
        if (null == config) {
            log.error("Unable to load agent config, please check config file: {}", Constants.DEFAULT_CONFIG_FILE);
            System.exit(1);
        }
        loadAndMonitorApps();

        try {
            WebSocketClientManager.getManager().init(config);
            heartbeat(config);
        } catch (Exception e) {
            log.error("Failed to start websocket client", e);
        }

        // load registries from master
        loadRegistries(config);
    }

    private static void loadRegistries(AgentConfig config) {
        WebSocketClientManager.getManager().getClient().send(RegistryReqMsg.builder()
                .type(BaseMsg.TYPE_REGISTRIES_REQ)
                .agentId(config.getAgent().getId())
                .build().toJsonString());
    }

    /**
     * 加载并开始监控应用状态（应用容器异常时进行恢复）
     */
    private static void loadAndMonitorApps() {
        File file = new File(Constants.APP_INSTANCE_INFO_FILE);
        if (!file.exists() || file.length() <= 0) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                log.error("Failed to create app instance store file {}", file, e);
            }
            return;
        }
        try (InputStream is = new FileInputStream(file)) {
            AppInstances appInstances = YamlUtil.loadAs(is, AppInstances.class);
            if (null != appInstances && CollectionUtils.isNotEmpty(appInstances.getInstances())) {
                appInstances.getInstances().forEach(instance -> {
                    AppManager.getManager().addInstance(instance.getContainerName(), instance);
                });
            }
        } catch (IOException e) {
            log.error("Failed to create inputstream from file {}", file, e);
        }

        // 边缘应用实例监控任务
        workers.scheduleWithFixedDelay(() -> {
            try {
                AppManager.getManager().listInstances().forEach(instance -> {
                    String containerId = instance.getContainerId();
                    String containerName = instance.getContainerName();
                    Optional<String> status = DockerUtil.status(containerId);
                    if (status.isEmpty()) {
                        // 再次检查应用是否被删除，未被删除才需要重新创建容器
                        if (AppManager.getManager().checkContainerExistence(containerName)) {
                            log.warn("Container {} not exists, redeploying app {}", containerId,
                                    instance.getAppName());
                            AppManager.getManager().removeInstance(containerName);

                            String id = DockerUtil.run(instance.getImage(),
                                    instance.getRegistryId(),
                                    containerName,
                                    instance.getEnv(),
                                    instance.getPortMappings(),
                                    instance.getVolumeMappings());

                            instance.setContainerId(id);
                            AppManager.getManager().addInstance(containerName, instance);
                        }
                    } else if (!Objects.equals(status.get(), "running")) {
                        log.warn("Container {} is not running, trying to restart it", containerId);
                        DockerUtil.restartContainer(containerId);
                    }
                });

                try (Writer writer = new FileWriter(file)) {
                    AppInstances appInstances = AppInstances.builder()
                            .instances(AppManager.getManager().listInstances())
                            .build();
                    writer.write(YamlUtil.dumpAsMap(appInstances));
                } catch (IOException e) {
                    log.error("Failed to create writer from file {}", file, e);
                }
            } catch (Exception e) {
                log.error("Failed to monitor app containers", e);
            }
        }, 3L, 10L, TimeUnit.SECONDS);
    }

    /**
     * 尝试加载agent配置
     *
     * @return
     */
    private static AgentConfig loadAgentConfig() {
        File file = new File(Constants.DEFAULT_CONFIG_FILE);
        if (!file.exists()) {
            return null;
        }
        try (InputStream is = new FileInputStream(file)) {
            return YamlUtil.loadAs(is, AgentConfig.class);
        } catch (IOException e) {
            log.error("Failed to create inputstream from file {}", file, e);
        }

        return null;
    }

    /**
     * 启动定时心跳
     *
     * @param config
     */
    private static void heartbeat(AgentConfig config) {
        workers.scheduleWithFixedDelay(() -> {
            try {
                WebSocketClientManager.getManager().getClient().send(HeartbeatMsg.builder()
                        .type(BaseMsg.TYPE_HEARTBEAT)
                        .token(config.getAgent().getToken())
                        .agentId(config.getAgent().getId())
                        .appInstances(AppManager.getManager().listInstances())
                        .build().toJsonString());
            } catch (Exception e) {
                log.error("Failed to send heartbeat message to master", e);
                WebSocketClientManager.getManager().reconnect();
            }
        }, 1000L, 10000L, TimeUnit.MILLISECONDS);
    }
}
