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
 * 重启应用动作实现
 *
 * @author xuxiuyou
 * @date 2023/8/12 12:26
 */
@Component("restartAction")
@Slf4j
public class RestartAction extends AbstractAction {
    @Override
    public Integer accept(AppTaskDetail appTaskDetail, Integer retries, CountDownLatch latch) {
        try {
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
            NodeApp nodeApp = null;
            if (CollectionUtils.isNotEmpty(nodeApps)) {
                nodeApps.sort((o1, o2) -> -o1.getCreateTime().compareTo(o2.getCreateTime()));
                nodeApp = nodeApps.get(0);
            }
            if (null == nodeApp) {
                log.error("App {} version not found on host {}",
                        app.getName(), appTaskDetail.getNodeId());
                appTaskDetail.setResult(AppTaskResult.FAIL.getResult());
                appTaskDetail.setReason(String.format("节点[%s]上不存在应用[%s]的已部署版本",
                        appTaskDetail.getNodeId(), app.getName()));
                appTaskDetailMapper.update(appTaskDetail);

                throw new RuntimeException(String.format("App %s version not found on host %s",
                        app.getName(), appTaskDetail.getNodeId()));
            }

            AppVersion appVersion = appVersionMapper.queryById(nodeApp.getVersionId());
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

            log.info("Restart host {} app {}", appTaskDetail.getNodeId(), appTaskDetail.getAppId());

            // 发送更新应用请求
            appTaskDetail.setStatus(NodeAppTaskStatus.RESTARTING.getStatus());
            appTaskDetailMapper.update(appTaskDetail);
            JSONObject jo = JSON.parseObject(appVersion.getRunningConfig());
            JSONArray env = jo.getJSONArray("env");
            boolean update = appService.restartApp((String) node.getId(), (String) nodeApp.getId());
            if (update) {
                appTaskDetail.setStatus(NodeAppTaskStatus.RESTART_END.getStatus());
                appTaskDetail.setResult(AppTaskResult.SUCCESS.getResult());
            } else {
                log.warn("Failed to update app {} on host {}",
                        app.getName(), appTaskDetail.getNodeId());
                appTaskDetail.setReason(String.format("重启节点[%s]应用[%s]失败",
                        appTaskDetail.getNodeId(), app.getName()));
                appTaskDetail.setResult(AppTaskResult.FAIL.getResult());
            }
            appTaskDetailMapper.update(appTaskDetail);
        } finally {
            latch.countDown();
        }
        return appTaskDetail.getResult();
    }
}
