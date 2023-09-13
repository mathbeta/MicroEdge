package com.mathbeta.microedge.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mathbeta.alphaboot.entity.Result;
import com.mathbeta.alphaboot.service.impl.BaseService;
import com.mathbeta.microedge.entity.*;
import com.mathbeta.microedge.enums.AppTaskResult;
import com.mathbeta.microedge.mapper.AppMapper;
import com.mathbeta.microedge.mapper.AppTaskDetailMapper;
import com.mathbeta.microedge.mapper.AppVersionMapper;
import com.mathbeta.microedge.mapper.NodeAppMapper;
import com.mathbeta.microedge.service.IAppService;
import com.mathbeta.microedge.service.INodeAppService;
import com.mathbeta.microedge.utils.JsonUtil;
import com.mathbeta.microedge.utils.WebSocketManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 应用信息
 *
 * @author xuxiuyou
 * @date 2023/09/07
 */
@Slf4j
@Service
public class AppService extends BaseService<App> implements IAppService {
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

            WebSocketManager.getManager().sendMsg(nodeId, JSON.toJSONString(RunAppMsg.builder()
                    .type(BaseMsg.TYPE_RUN_APP)
                    .appName(app.getName())
                    .containerName(containerName)
                    .image(appVersion.getImage())
                    .registryId("2")
                    .env(JsonUtil.jsonArrayToList(env))
                    .portMappings(JsonUtil.jsonArrayToList(portMappings))
                    .volumeMappings(JsonUtil.jsonArrayToList(volumeMappings))
                    .build()));
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
            String containerName = String.format("microedge_%s", nodeAppId);
            WebSocketManager.getManager().sendMsg(nodeId, JSON.toJSONString(RemoveAppMsg.builder()
                    .type(BaseMsg.TYPE_REMOVE_APP)
                    .containerName(containerName)
                    .build()));
            return true;
        } catch (Exception e) {
            log.error("Failed to send remove app message to agent {}", nodeId, e);
        }
        return false;
    }

    @Override
    public boolean restartApp(String nodeId, String nodeAppId) {
        try {
            String containerName = String.format("microedge_%s", nodeAppId);
            WebSocketManager.getManager().sendMsg(nodeId, JSON.toJSONString(RestartAppMsg.builder()
                    .type(BaseMsg.TYPE_RESTART_APP)
                    .containerName(containerName)
                    .build()));
            return true;
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
                    Map<String, Object> params = Maps.newHashMap();
                    params.put("nodeId", nodeId);
                    params.put("appId", e.getAppId());
                    params.put("enabled", true);
                    List<NodeApp> nodeApps = nodeAppMapper.query(params);
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
                    String containerName = String.format("microedge_%s", instanceId);

                    App app = appMapper.queryById(e.getAppId());
                    AppVersion appVersion = appVersionMapper.queryById(e.getAppVersionId());
                    JSONObject jo = JSON.parseObject(appVersion.getRunningConfig());

                    upgradeConfigs.add(RunAppMsg.builder()
                            .type(BaseMsg.TYPE_UPGRADE_APP)
                            .appName(app.getName())
                            .containerName(containerName)
                            .image(appVersion.getImage())
                            .registryId("2")
                            .env(JsonUtil.jsonArrayToList(jo.getJSONArray("env")))
                            .portMappings(JsonUtil.jsonArrayToList(jo.getJSONArray("portMappings")))
                            .volumeMappings(JsonUtil.jsonArrayToList(jo.getJSONArray("volumeMappings")))
                            .build());

                    // todo 执行成功后再更新AppTaskDetail结果
                    params.clear();
                    params.put("taskId", upgradeTaskId);
                    params.put("appId", e.getAppId());
                    params.put("nodeId", nodeId);
                    params.put("versionId", e.getAppVersionId());


                    List<AppTaskDetail> appTaskDetails = appTaskDetailMapper.query(params);
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
            WebSocketManager.getManager().sendMsg(nodeId, JSON.toJSONString(UpgradeAppMsg.builder()
                    .type(BaseMsg.TYPE_UPGRADE_APP)
                    .upgradeConfigs(upgradeConfigs)
                    .build()));
            return true;
        } catch (Exception e) {
            log.error("Failed to send remove app message to agent {}", nodeId, e);
        }
        return false;
    }

    @Override
    public Result listApps(String nodeId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("nodeId", nodeId);
        params.put("enabled", true);
        List<NodeApp> nodeApps = nodeAppMapper.query(params);
        if (CollectionUtils.isNotEmpty(nodeApps)) {
            return Result.success(nodeApps.stream().map(e -> {
                App app = appMapper.queryById(e.getAppId());
                AppVersion appVersion = appVersionMapper.queryById(e.getVersionId());
                return AppInstance.builder()
                        .appName(app.getName())
                        .nodeId(e.getNodeId())
                        .containerName(String.format("microedge_%s", e.getId()))
                        .image(appVersion.getImage())
                        .versionNum(appVersion.getVersionNum())
                        .build();
            }).collect(Collectors.toList()));
        }
        return Result.success(Lists.newArrayList());
    }
}
