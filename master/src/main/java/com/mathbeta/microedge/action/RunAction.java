package com.mathbeta.microedge.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mathbeta.alphaboot.utils.ParamBuilder;
import com.mathbeta.microedge.entity.*;
import com.mathbeta.microedge.enums.AppTaskResult;
import com.mathbeta.microedge.enums.NodeAppTaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 部署应用动作实现
 *
 * @author xuxiuyou
 * @date 2023/8/12 12:28
 */
@Component("runAction")
@Slf4j
public class RunAction extends AbstractAction {
    @Override
    public Integer accept(AppTaskDetail appTaskDetail, Integer retries, CountDownLatch latch) {
        try {
            appTaskDetail.setStatus(NodeAppTaskStatus.DEPLOYING.getStatus());
            appTaskDetailMapper.update(appTaskDetail);

            Node node = nodeMapper.queryById(appTaskDetail.getNodeId());
            if (null == node) {
                log.error("Host not exists with id {}", appTaskDetail.getNodeId());
                appTaskDetail.setResult(AppTaskResult.FAIL.getResult());
                appTaskDetail.setReason(String.format("该id节点不存在：%s", appTaskDetail.getNodeId()));
                appTaskDetailMapper.update(appTaskDetail);

                throw new RuntimeException(String.format("Host not exists with id %s", appTaskDetail.getNodeId()));
            }

            App app = appMapper.queryById(appTaskDetail.getAppId());
            List<NodeApp> nodeApps = nodeAppMapper.query(ParamBuilder.builder()
                    .put("nodeId", appTaskDetail.getNodeId())
                    .put("appId", appTaskDetail.getAppId())
                    .put("enabled", true)
                    .build());
            NodeApp nodeApp;
            if (CollectionUtils.isNotEmpty(nodeApps)) {
                nodeApps.sort((o1, o2) -> -o1.getCreateTime().compareTo(o2.getCreateTime()));
                nodeApp = nodeApps.get(0);
            } else {
                nodeApp = null;
            }
            if (null != nodeApp) {
                log.error("App {} version already exists on host {}",
                        app.getName(), appTaskDetail.getNodeId());
                appTaskDetail.setResult(AppTaskResult.FAIL.getResult());
                appTaskDetail.setReason(String.format("主机[%s]上已存在应用[%s]的已部署版本[%s]，不可再次部署，可执行升级",
                        appTaskDetail.getNodeId(), app.getName(), nodeApp.getVersionId()));
                appTaskDetailMapper.update(appTaskDetail);

                throw new RuntimeException(String.format("App %s version already exists on host %s",
                        app.getName(), appTaskDetail.getNodeId()));
            }

            AppVersion appVersion = appVersionMapper.queryById(appTaskDetail.getVersionId());
            if (null == appVersion) {
                log.error("App {} version {} not exists",
                        app.getName(), nodeApp.getVersionId());
                appTaskDetail.setResult(AppTaskResult.FAIL.getResult());
                appTaskDetail.setReason(String.format("应用[%s]版本[%s]不存在",
                        app.getName(), nodeApp.getVersionId()));
                appTaskDetailMapper.update(appTaskDetail);

                throw new RuntimeException(String.format("App %s version %s not exists",
                        app.getName(), nodeApp.getVersionId()));
            }

//            params.clear();
//            params.put("nodeId", appTaskDetail.getNodeId());
//            params.put("enabled", true);
//            List<NodeApp> nodeAppList = nodeAppMapper.query(params);
//            List<App> appList = Lists.newArrayList(app);
//            List<AppVersion> appVersionList = Lists.newArrayList(appVersion);
//            if (CollectionUtils.isNotEmpty(nodeAppList)) {
//                nodeAppList.forEach(link -> {
//                    appList.add(appMapper.queryById(link.getAppId()));
//                    appVersionList.add(appVersionMapper.queryById(link.getVersionId()));
//                });
//            }

            try {
                JSONObject jo = JSON.parseObject(appVersion.getRunningConfig());
                JSONArray env = jo.getJSONArray("env");
                // 发送部署应用请求
                DeploymentInfo deploymentInfo = appService.runApp((String) app.getId(),
                        (String) node.getId(),
                        appVersion,
                        env,
                        jo.getJSONArray("portMappings"),
                        jo.getJSONArray("volumeMappings"));
                appTaskDetail.setStatus(NodeAppTaskStatus.DEPLOY_ENDED.getStatus());
                if (null != deploymentInfo && deploymentInfo.isResult()) {
                    appTaskDetail.setResult(AppTaskResult.SUCCESS.getResult());
                } else {
                    log.warn("Failed to deploy app {} on host {}",
                            app.getName(), appTaskDetail.getNodeId());
                    appTaskDetail.setReason(String.format("部署节点[%s]应用[%s]失败",
                            appTaskDetail.getNodeId(), app.getName()));
                    appTaskDetail.setResult(AppTaskResult.FAIL.getResult());
                }
                appTaskDetailMapper.update(appTaskDetail);
            } catch (Exception e) {
                log.error("Failed to run app {}", app.getName(), e);
            }
        } finally {
            latch.countDown();
        }
        return appTaskDetail.getResult();
    }
}
