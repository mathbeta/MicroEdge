package com.mathbeta.microedge.action;

import com.google.common.collect.Maps;
import com.mathbeta.microedge.entity.App;
import com.mathbeta.microedge.entity.AppTaskDetail;
import com.mathbeta.microedge.entity.Node;
import com.mathbeta.microedge.entity.NodeApp;
import com.mathbeta.microedge.enums.AppTaskResult;
import com.mathbeta.microedge.enums.NodeAppTaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * 删除应用动作实现
 *
 * @author xuxiuyou
 * @date 2023/8/12 12:28
 */
@Component("removeAction")
@Slf4j
public class RemoveAction extends AbstractAction {
    @Override
    public Integer accept(AppTaskDetail appTaskDetail, Integer retries, CountDownLatch latch) {
        try {
            appTaskDetail.setStatus(NodeAppTaskStatus.REMOVING.getStatus());
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
            Map<String, Object> params = Maps.newHashMap();
            params.put("nodeId", appTaskDetail.getNodeId());
            params.put("appId", appTaskDetail.getAppId());
            params.put("enabled", true);
            List<NodeApp> nodeApps = nodeAppMapper.query(params);
            NodeApp nodeApp;
            if (CollectionUtils.isNotEmpty(nodeApps)) {
                nodeApps.sort((o1, o2) -> -o1.getCreateTime().compareTo(o2.getCreateTime()));
                nodeApp = nodeApps.get(0);
            } else {
                nodeApp = null;
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

            params.clear();
            params.put("nodeId", appTaskDetail.getNodeId());
            params.put("enabled", true);

//            List<NodeApp> nodeAppList = nodeAppMapper.query(params);
//            List<App> appList = Lists.newArrayList();
//            List<AppVersion> appVersionList = Lists.newArrayList();
//            if (CollectionUtils.isNotEmpty(nodeAppList)) {
//                // 过滤掉待删除应用
//                nodeAppList.stream().filter(e -> !e.getAppId().equals(app.getId())
//                        && !e.getVersionId().equals(nodeApp.getVersionId())).forEach(link -> {
//                    appList.add(appMapper.queryById(link.getAppId()));
//                    appVersionList.add(appVersionMapper.queryById(link.getVersionId()));
//                });
//            }

            try {
                // 发送删除应用请求
                boolean remove = appService.removeApp((String) node.getId(), (String) nodeApp.getId());
                appTaskDetail.setVersionId(nodeApp.getVersionId());
                appTaskDetail.setStatus(NodeAppTaskStatus.REMOVE_ENDED.getStatus());
                if (remove) {
                    nodeApp.setEnabled(0);
                    nodeAppMapper.update(nodeApp);
                    appTaskDetail.setResult(AppTaskResult.SUCCESS.getResult());
                } else {
                    appTaskDetail.setResult(AppTaskResult.FAIL.getResult());
                    appTaskDetail.setReason(String.format("删除节点[%s]应用[%s]失败",
                            appTaskDetail.getNodeId(), app.getName()));
                    log.warn("Failed to remove app {} on host {}",
                            app.getName(), appTaskDetail.getNodeId());
                }
                appTaskDetailMapper.update(appTaskDetail);
            } catch (Exception e) {
                log.error("Failed to remove app {}", app.getName(), e);
            }
        } finally {
            latch.countDown();
        }
        return appTaskDetail.getResult();
    }
}
