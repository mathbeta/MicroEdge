package com.mathbeta.microedge.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.mathbeta.alphaboot.entity.Result;
import com.mathbeta.alphaboot.service.impl.BaseService;
import com.mathbeta.alphaboot.utils.ParamBuilder;
import com.mathbeta.microedge.entity.*;
import com.mathbeta.microedge.enums.AppTaskResult;
import com.mathbeta.microedge.mapper.AppMapper;
import com.mathbeta.microedge.mapper.AppTaskDetailMapper;
import com.mathbeta.microedge.mapper.AppVersionMapper;
import com.mathbeta.microedge.mapper.NodeAppMapper;
import com.mathbeta.microedge.service.IAppService;
import com.mathbeta.microedge.service.INodeAppService;
import com.mathbeta.microedge.utils.AppManager;
import com.mathbeta.microedge.utils.Constants;
import com.mathbeta.microedge.utils.JsonUtil;
import com.mathbeta.microedge.utils.WebSocketManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 应用信息
 *
 * @author xuxiuyou
 * @date 2023/09/07
 */
@Slf4j
@Service
public class AppService extends BaseService<App, AppMapper> implements IAppService {
    @Resource
    private AppMapper appMapper;

    @Resource
    private NodeAppMapper nodeAppMapper;

    @Resource
    private INodeAppService nodeAppService;

    @Resource
    private AppVersionMapper appVersionMapper;

    @Resource
    private AppTaskDetailMapper appTaskDetailMapper;

    @Override
    protected AppMapper getMapper() {
        return appMapper;
    }

    @Override
    public DeploymentInfo runApp(String appId, String nodeId, AppVersion appVersion,
                                 JSONArray env,
                                 JSONArray portMappings,
                                 JSONArray volumeMappings) {
        String instanceId = null;
        boolean result = true;
        try {
            NodeApp nodeApp = NodeApp.builder()
                    .appId(appId)
                    .versionId((String) appVersion.getId())
                    .nodeId(nodeId)
                    .enabled(1)
                    .build();
            nodeAppService.create(nodeApp);
            instanceId = (String) nodeApp.getId();
            String containerName = String.format("microedge_%s", instanceId);
            App app = appMapper.queryById(appId);

            result = WebSocketManager.getManager().sendMsg(nodeId, RunAppMsg.builder()
                    .type(BaseMsg.TYPE_RUN_APP)
                    .appName(app.getName())
                    .containerName(containerName)
                    .image(appVersion.getImage())
                    .registryId(appVersion.getRegistryId())
                    .env(JsonUtil.jsonArrayToList(env))
                    .portMappings(JsonUtil.jsonArrayToList(portMappings))
                    .volumeMappings(JsonUtil.jsonArrayToList(volumeMappings))
                    .build().toJsonString());
        } catch (Exception e) {
            log.error("Failed to send run app message to agent {}", nodeId, e);
            result = false;
        }
        return DeploymentInfo.builder()
                .appInstanceId(instanceId)
                .result(result)
                .build();
    }

    @Override
    public boolean removeApp(String nodeId, String nodeAppId) {
        try {
            String containerName = String.format(Constants.APP_CONTAINER_NAME_TEMPLATE, nodeAppId);
            return WebSocketManager.getManager().sendMsg(nodeId, RemoveAppMsg.builder()
                    .type(BaseMsg.TYPE_REMOVE_APP)
                    .containerName(containerName)
                    .build().toJsonString());
        } catch (Exception e) {
            log.error("Failed to send remove app message to agent {}", nodeId, e);
        }
        return false;
    }

    @Override
    public boolean restartApp(String nodeId, String nodeAppId) {
        try {
            String containerName = String.format(Constants.APP_CONTAINER_NAME_TEMPLATE, nodeAppId);
            return WebSocketManager.getManager().sendMsg(nodeId, RestartAppMsg.builder()
                    .type(BaseMsg.TYPE_RESTART_APP)
                    .containerName(containerName)
                    .build().toJsonString());
        } catch (Exception e) {
            log.error("Failed to send remove app message to agent {}", nodeId, e);
        }
        return false;
    }

    @Override
    public boolean upgradeApp(String nodeId, String upgradeTaskId, List<AppTaskConfig> appTaskConfigs) {
        try {
            List<RunAppMsg> upgradeConfigs = Lists.newArrayList();
            appTaskConfigs.forEach(e -> {
                try {
                    List<NodeApp> nodeApps = nodeAppMapper.query(ParamBuilder.builder()
                            .put("nodeId", nodeId)
                            .put("appId", e.getAppId())
                            .put("enabled", true)
                            .build());
                    NodeApp oldNodeApp;
                    if (CollectionUtils.isNotEmpty(nodeApps)) {
                        nodeApps.sort((o1, o2) -> -o1.getCreateTime().compareTo(o2.getCreateTime()));
                        oldNodeApp = nodeApps.get(0);
                    } else {
                        oldNodeApp = null;
                    }
                    if (null != oldNodeApp) {
                        oldNodeApp.setEnabled(0);
                        nodeAppMapper.update(oldNodeApp);
                    }

                    NodeApp nodeApp = NodeApp.builder()
                            .appId(e.getAppId())
                            .versionId(e.getAppVersionId())
                            .nodeId(nodeId)
                            .enabled(1)
                            .build();
                    nodeAppService.create(nodeApp);
                    String instanceId = (String) nodeApp.getId();
                    String containerName = String.format(Constants.APP_CONTAINER_NAME_TEMPLATE, instanceId);

                    App app = appMapper.queryById(e.getAppId());
                    AppVersion appVersion = appVersionMapper.queryById(e.getAppVersionId());
                    JSONObject jo = JSON.parseObject(appVersion.getRunningConfig());

                    upgradeConfigs.add(RunAppMsg.builder()
                            .type(BaseMsg.TYPE_UPGRADE_APP)
                            .appName(app.getName())
                            .containerName(containerName)
                            .image(appVersion.getImage())
                            .registryId(appVersion.getRegistryId())
                            .env(JsonUtil.jsonArrayToList(jo.getJSONArray("env")))
                            .portMappings(JsonUtil.jsonArrayToList(jo.getJSONArray("portMappings")))
                            .volumeMappings(JsonUtil.jsonArrayToList(jo.getJSONArray("volumeMappings")))
                            .build());

                    // todo 执行成功后再更新AppTaskDetail结果
                    List<AppTaskDetail> appTaskDetails = appTaskDetailMapper.query(ParamBuilder.builder()
                            .put("taskId", upgradeTaskId)
                            .put("appId", e.getAppId())
                            .put("nodeId", nodeId)
                            .put("versionId", e.getAppVersionId())
                            .build());
                    AppTaskDetail appTaskDetail = null;
                    if (CollectionUtils.isNotEmpty(appTaskDetails)) {
                        appTaskDetail = appTaskDetails.get(0);
                        appTaskDetail.setResult(AppTaskResult.SUCCESS.getResult());
                        appTaskDetailMapper.update(appTaskDetail);
                    }
                } catch (Exception ex) {
                    log.error("Failed to create node app link", ex);
                }
            });
            return WebSocketManager.getManager().sendMsg(nodeId, UpgradeAppMsg.builder()
                    .type(BaseMsg.TYPE_UPGRADE_APP)
                    .upgradeConfigs(upgradeConfigs)
                    .build().toJsonString());
        } catch (Exception e) {
            log.error("Failed to send remove app message to agent {}", nodeId, e);
        }
        return false;
    }

    @Override
    public Result listApps(String nodeId) {
        List<NodeApp> nodeApps = nodeAppMapper.query(ParamBuilder.builder()
                .put("nodeId", nodeId)
                .put("enabled", true)
                .build());
        if (CollectionUtils.isNotEmpty(nodeApps)) {
            return Result.success(nodeApps.stream().map(e -> {
                App app = appMapper.queryById(e.getAppId());
                AppVersion appVersion = appVersionMapper.queryById(e.getVersionId());
                String containerName = String.format(Constants.APP_CONTAINER_NAME_TEMPLATE, e.getId());
                Optional<AppInstance> instance = AppManager.getManager().queryByContainerName(containerName);

                return AppInstance.builder()
                        .appName(app.getName())
                        .nodeId(e.getNodeId())
                        .status(instance.map(AppInstance::getStatus).orElse("not exist"))
                        .containerId(instance.map(AppInstance::getContainerId).orElse(null))
                        .containerName(containerName)
                        .image(appVersion.getImage())
                        .versionNum(appVersion.getVersionNum())
                        .registryId(instance.map(AppInstance::getRegistryId).orElse(null))
                        .env(instance.map(AppInstance::getEnv).orElse(null))
                        .portMappings(instance.map(AppInstance::getPortMappings).orElse(null))
                        .volumeMappings(instance.map(AppInstance::getVolumeMappings).orElse(null))
                        .build();
            }).collect(Collectors.toList()));
        }
        return Result.success(Lists.newArrayList());
    }
}
