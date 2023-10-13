package com.mathbeta.microedge.ws;

import com.alibaba.fastjson.JSON;
import com.mathbeta.microedge.config.Constants;
import com.mathbeta.microedge.entity.*;
import com.mathbeta.microedge.utils.AppManager;
import com.mathbeta.microedge.utils.DockerRegistryManager;
import com.mathbeta.microedge.utils.DockerUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.java_websocket.client.WebSocketClient;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * 与云端管控服务之间的websocket交互消息处理
 *
 * @author xuxiuyou
 * @date 2023/9/8 17:41
 */
@Slf4j
public class WebSocketMessageConsumer implements BiConsumer<WebSocketClient, String> {
    @Override
    public void accept(WebSocketClient webSocketClient, String msg) {
        log.info("Received message {} from peer {}", msg, webSocketClient.getRemoteSocketAddress());
        BaseMsg baseMsg = JSON.parseObject(msg, BaseMsg.class);
        if (null != baseMsg) {
            switch (baseMsg.getType()) {
                case BaseMsg.TYPE_HEARTBEAT:
                    log.debug("Received heartbeat message ack, ignoring");
                    break;

                case BaseMsg.TYPE_REGISTRIES_RESP:
                    loadRegistries(msg);
                    break;
                case BaseMsg.TYPE_RUN_APP:
                    runApp(msg);
                    break;

                case BaseMsg.TYPE_REMOVE_APP:
                    removeApp(msg);
                    break;

                case BaseMsg.TYPE_RESTART_APP:
                    restartApp(msg);
                    break;

                case BaseMsg.TYPE_UPGRADE_APP:
                    upgradeApp(msg);
                    break;

                default:
                    log.warn("Unknown message type {} of msg {}", baseMsg.getType(), msg);
            }
        }
    }

    private void loadRegistries(String msg) {
        RegistryRespMsg registryRespMsg = JSON.parseObject(msg, RegistryRespMsg.class);
        if (null != registryRespMsg) {
            loadRegistries(registryRespMsg);
        }
    }

    private void loadRegistries(RegistryRespMsg registryRespMsg) {
        if (null != registryRespMsg && CollectionUtils.isNotEmpty(registryRespMsg.getRegistries())) {
            registryRespMsg.getRegistries().forEach(r -> {
                DockerRegistryManager.getManager().addRegistry(r.getId(), r);
            });
        }
    }

    private void runApp(String msg) {
        RunAppMsg runAppMsg = JSON.parseObject(msg, RunAppMsg.class);
        if (null != runAppMsg) {
            runApp(runAppMsg);
        }
    }

    private void runApp(RunAppMsg runAppMsg) {
        String containerName = runAppMsg.getContainerName();
        log.info("Running container {} for image {} with: env={}, portMappings={}, volumeMappings={}",
                containerName,
                runAppMsg.getImage(),
                runAppMsg.getEnv(),
                runAppMsg.getPortMappings(),
                runAppMsg.getVolumeMappings());
        if (!DockerUtil.existLocalImage(runAppMsg.getImage(), runAppMsg.getRegistryId())) {
            DockerUtil.pullImage(runAppMsg.getImage(), runAppMsg.getRegistryId());
        }

        String id = DockerUtil.run(runAppMsg.getImage(),
                runAppMsg.getRegistryId(),
                containerName,
                runAppMsg.getEnv(),
                runAppMsg.getPortMappings(),
                runAppMsg.getVolumeMappings());

        AppManager.getManager().addInstance(containerName, AppInstance.builder()
                .appName(runAppMsg.getAppName())
                .containerId(id)
                .status(Constants.APP_STATUS_RUNNING)
                .containerName(containerName)
                .image(runAppMsg.getImage())
                .registryId(runAppMsg.getRegistryId())
                .env(runAppMsg.getEnv())
                .portMappings(runAppMsg.getPortMappings())
                .volumeMappings(runAppMsg.getVolumeMappings())
                .build());
    }

    private void removeApp(String msg) {
        RemoveAppMsg removeAppMsg = JSON.parseObject(msg, RemoveAppMsg.class);
        if (null != removeAppMsg) {
            String containerName = removeAppMsg.getContainerName();
            log.info("Removing container {}", containerName);
            DockerUtil.removeContainer(containerName);
            AppManager.getManager().removeInstance(containerName);
        }
    }

    private void restartApp(String msg) {
        RestartAppMsg restartAppMsg = JSON.parseObject(msg, RestartAppMsg.class);
        if (null != restartAppMsg) {
            log.info("Restarting container {}", restartAppMsg.getContainerName());
            DockerUtil.restartContainer(restartAppMsg.getContainerName());
        }
    }

    private void upgradeApp(String msg) {
        UpgradeAppMsg upgradeAppMsg = JSON.parseObject(msg, UpgradeAppMsg.class);
        if (null != upgradeAppMsg) {
            List<RunAppMsg> upgradeConfigs = upgradeAppMsg.getUpgradeConfigs();
            if (CollectionUtils.isNotEmpty(upgradeConfigs)) {
                upgradeConfigs.forEach(e -> {
                    String id = AppManager.getManager().queryContainerId(e.getAppName());
                    if (null != id) {
                        AppManager.getManager().removeInstance(id);
                        DockerUtil.removeContainer(id);
                    }

                    runApp(e);
                });
            }
        }
    }
}
