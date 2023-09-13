package com.mathbeta.microedge.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mathbeta.microedge.entity.*;
import com.mathbeta.microedge.enums.AppTaskResult;
import com.mathbeta.microedge.enums.NodeAppTaskStatus;
import com.mathbeta.microedge.mapper.AppMapper;
import com.mathbeta.microedge.mapper.AppTaskDetailMapper;
import com.mathbeta.microedge.mapper.AppVersionMapper;
import com.mathbeta.microedge.mapper.NodeAppMapper;
import com.mathbeta.microedge.service.IAppService;
import com.mathbeta.microedge.service.INodeAppService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 升级应用动作实现
 *
 * @author xuxiuyou
 * @date 2023/8/12 12:28
 */
@Component("upgradeAction")
@Slf4j
public class UpgradeAction implements IAppAction<AppUpgradeConfirmReq, Node, List<AppTaskConfig>, Integer> {
    @Resource
    protected INodeAppService nodeAppService;
    @Resource
    protected IAppService appService;
    @Resource
    private AppTaskDetailMapper appTaskDetailMapper;
    @Resource
    private AppMapper appMapper;
    @Resource
    private AppVersionMapper appVersionMapper;
    @Resource
    private NodeAppMapper nodeAppMapper;

    @Override
    public Integer accept(AppUpgradeConfirmReq appUpgradeConfirmReq, Node node,
                          List<AppTaskConfig> appTaskConfigs) {
        String nodeId = (String) node.getId();
        String upgradeTaskId = appUpgradeConfirmReq.getUpgradeTaskId();
        if (CollectionUtils.isNotEmpty(appTaskConfigs)) {
            Boolean accepted = appUpgradeConfirmReq.getAccepted();
            // 拒绝本次升级
            if (null != accepted && !accepted) {
                for (AppTaskConfig appTaskConfig : appTaskConfigs) {
                    Map<String, Object> params = Maps.newHashMap();
                    params.put("nodeId", node);
                    params.put("appId", appTaskConfig.getAppId());
                    params.put("taskId", upgradeTaskId);
                    List<AppTaskDetail> appTaskDetails = appTaskDetailMapper.query(params);
                    if (CollectionUtils.isNotEmpty(appTaskDetails)) {
                        AppTaskDetail appTaskDetail = appTaskDetails.get(0);
                        appTaskDetail.setAccepted(0);
                        appTaskDetailMapper.update(appTaskDetail);
                    }
                }
                return AppTaskResult.SUCCESS.getResult();
            }

            // 先保存旧版本信息，以进行还原
            List<App> appList = Lists.newArrayList();
            List<AppVersion> appOldVersionList = Lists.newArrayList();
            List<AppVersion> appNewVersionList = Lists.newArrayList();
            List<NodeApp> nodeAppList = Lists.newArrayList();
            for (AppTaskConfig appTaskConfig : appTaskConfigs) {
                Map<String, Object> params = Maps.newHashMap();
                params.put("nodeId", nodeId);
                params.put("appId", appTaskConfig.getAppId());
                params.put("enabled", true);
                List<NodeApp> nodeApps = nodeAppMapper.query(params);
                NodeApp nodeApp;
                if (CollectionUtils.isNotEmpty(nodeApps)) {
                    nodeApps.sort((o1, o2) -> -o1.getCreateTime().compareTo(o2.getCreateTime()));
                    nodeApp = nodeApps.get(0);
                } else {
                    nodeApp = null;
                }

                appList.add(appMapper.queryById(appTaskConfig.getAppId()));
                appOldVersionList.add(appVersionMapper.queryById(nodeApp.getVersionId()));
                appNewVersionList.add(appVersionMapper.queryById(appTaskConfig.getAppVersionId()));
                nodeAppList.add(nodeApp);
            }

            Map<String, Object> params = Maps.newHashMap();
            params.put("nodeId", node.getId());
            params.put("enabled", true);
            List<NodeApp> nodeEnabledAppList = nodeAppMapper.query(params);
            List<App> recoveryAppList = Lists.newArrayList();
            List<AppVersion> recoveryAppOldVersionList = Lists.newArrayList();
            List<AppVersion> recoveryAppNewVersionList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(nodeEnabledAppList)) {
                nodeEnabledAppList.forEach(link -> {
                    int j = nodeAppList.indexOf(link);
                    // 待升级应用，新旧版本不同
                    if (j >= 0) {
                        recoveryAppList.add(appList.get(j));
                        recoveryAppOldVersionList.add(appOldVersionList.get(j));
                        recoveryAppNewVersionList.add(appNewVersionList.get(j));
                    } else {
                        recoveryAppList.add(appMapper.queryById(link.getAppId()));
                        AppVersion appVersion = appVersionMapper.queryById(link.getVersionId());
                        recoveryAppOldVersionList.add(appVersion);
                        recoveryAppNewVersionList.add(appVersion);
                    }
                });
            }

            appService.upgradeApp((String) node.getId(), upgradeTaskId, appTaskConfigs);

//            try {
//                // 开始升级各应用
//                boolean rollback = false;
//                int i = 0;
//                for (; i < appTaskConfigs.size(); i++) {
//                    rollback = upgradeApp(appTaskConfigs.get(i),
//                            node,
//                            appUpgradeConfirmReq.getUpgradeTaskId(),
//                            appList.get(i),
//                            appNewVersionList.get(i),
//                            nodeAppList.get(i));
//                    if (rollback) {
//                        break;
//                    }
//                }
//                if (rollback) {
//                    // 按逆序还原各应用
//                    for (int j = i - 1; j >= 0; j--) {
//                        rollback(appUpgradeConfirmReq,
//                                node,
//                                appTaskConfigs.get(j),
//                                appList.get(j),
//                                appOldVersionList.get(j),
//                                nodeAppList.get(j));
//                    }
//                }
//            } catch (Exception e) {
//                log.error("Failed to upgrade app on node {}", node.getId(), e);
//            }
        } else {
            log.warn("No upgrade task config found for task id {}, nothing to do", upgradeTaskId);
        }

        return AppTaskResult.SUCCESS.getResult();
    }

    /**
     * 回退应用版本
     *
     * @param appUpgradeConfirmReq
     * @param node
     * @param appTaskConfig
     * @param app
     * @param appVersion
     * @param nodeApp
     */
    private void rollback(AppUpgradeConfirmReq appUpgradeConfirmReq,
                          Node node,
                          AppTaskConfig appTaskConfig,
                          App app,
                          AppVersion appVersion,
                          NodeApp nodeApp) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("nodeId", node.getId());
        params.put("appId", appTaskConfig.getAppId());
        params.put("taskId", appUpgradeConfirmReq.getUpgradeTaskId());
        List<AppTaskDetail> appTaskDetails = appTaskDetailMapper.query(params);
        AppTaskDetail appTaskDetail = null;
        if (CollectionUtils.isNotEmpty(appTaskDetails)) {
            appTaskDetail = appTaskDetails.get(0);
        }

        appTaskDetail.setStatus(NodeAppTaskStatus.ROLLBACK.getStatus());
        appTaskDetailMapper.update(appTaskDetail);

        JSONObject jo = JSON.parseObject(appVersion.getRunningConfig());
        JSONArray env = jo.getJSONArray("env");
        boolean update = true;
        if (update) {
            appTaskDetail.setStatus(NodeAppTaskStatus.ROLLBACK_ENDED.getStatus());
            appTaskDetail.setResult(AppTaskResult.SUCCESS.getResult());
        } else {
            log.error("Failed to update app {} on node {}", app.getName(), appTaskDetail.getNodeId());
            appTaskDetail.setReason(String.format("还原节点[%s]应用[%s]失败",
                    appTaskDetail.getNodeId(), app.getName()));
            appTaskDetail.setResult(AppTaskResult.FAIL.getResult());
        }
        appTaskDetailMapper.update(appTaskDetail);
    }

    /**
     * 升级应用
     *
     * @param appTaskConfig
     * @param node
     * @param upgradeTaskId
     * @param app
     * @param appVersion
     * @param nodeApp
     * @return
     */
    private boolean upgradeApp(AppTaskConfig appTaskConfig,
                               Node node, String upgradeTaskId, App app,
                               AppVersion appVersion, NodeApp nodeApp) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("nodeId", node.getId());
        params.put("appId", appTaskConfig.getAppId());
        params.put("taskId", upgradeTaskId);
        List<AppTaskDetail> appTaskDetails = appTaskDetailMapper.query(params);
        AppTaskDetail appTaskDetail = null;
        if (CollectionUtils.isNotEmpty(appTaskDetails)) {
            appTaskDetail = appTaskDetails.get(0);
        }

        appTaskDetail.setAccepted(1);
        appTaskDetail.setStatus(NodeAppTaskStatus.REMOVING.getStatus());
        appTaskDetailMapper.update(appTaskDetail);


        appTaskDetail.setStatus(NodeAppTaskStatus.UPGRADING.getStatus());
        appTaskDetailMapper.update(appTaskDetail);


        log.info("Upgrade host {} app {}", appTaskDetail.getNodeId(), appTaskDetail.getAppId());
        JSONObject jo = JSON.parseObject(appVersion.getRunningConfig());
        JSONArray env = jo.getJSONArray("env");
        boolean update = true;
        if (update) {
            appTaskDetail.setStatus(NodeAppTaskStatus.UPGRADE_ENDED.getStatus());
            appTaskDetail.setResult(AppTaskResult.SUCCESS.getResult());

            nodeApp.setEnabled(0);
            nodeAppMapper.update(nodeApp);

            // 记录新版本应用与节点的关联关系
//            NodeApp link = NodeApp.builder()
//                    .nodeId(appTaskDetail.getNodeId())
//                    .appId(appTaskDetail.getAppId())
//                    .versionId(appTaskDetail.getVersionId())
//                    .appInstanceId(nodeApp.getAppInstanceId())
//                    .build();
//            link.setEnabled(1);
//            nodeAppMapper.create(link);

//            AppContainerStatus status = EcnOperatorHelper.getOperator().status(hostExtInfoDO.getNodeUuid(),
//                    hostAppVersionLinkDO.getEcnUuid(), appInfoDO.getAppName(), appVersionInfoDO.getImageName());
//            int i = 0;
//            while (i < 40 && AppContainerStatus.RUNNING == status) {
//                sleep(1000L);
//                i++;
//                status = EcnOperatorHelper.getOperator().status(hostExtInfoDO.getNodeUuid(),
//                        hostAppVersionLinkDO.getEcnUuid(), appInfoDO.getAppName(), appVersionInfoDO.getImageName());
//            }
//            i = 0;
//            while (i < 5 && AppContainerStatus.RUNNING != status) {
//                sleep(3000L);
//                i++;
//                status = EcnOperatorHelper.getOperator().status(hostExtInfoDO.getNodeUuid(),
//                        hostAppVersionLinkDO.getEcnUuid(), appInfoDO.getAppName(), appVersionInfoDO.getImageName());
//            }
//            if (AppContainerStatus.RUNNING != status) {
//                log.warn("App {} container on host {} is not running after upgrade", appInfoDO.getAppName(),
//                        hostExtInfoDO.getId());
//            }
        } else {
            log.warn("Failed to update app {} on host {}",
                    app.getName(), appTaskDetail.getNodeId());
            appTaskDetail.setReason(String.format("升级节点[%s]应用[%s]失败",
                    appTaskDetail.getNodeId(), app.getName()));
            appTaskDetail.setResult(AppTaskResult.FAIL.getResult());
            // 升级失败需要回滚
            return true;
        }
        appTaskDetailMapper.update(appTaskDetail);

        return false;
    }
}
