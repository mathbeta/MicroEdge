package com.mathbeta.microedge.service.impl;


import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mathbeta.alphaboot.entity.ErrorInfo;
import com.mathbeta.alphaboot.entity.Result;
import com.mathbeta.alphaboot.exception.BusinessException;
import com.mathbeta.alphaboot.service.impl.BaseService;
import com.mathbeta.microedge.action.IAppAction;
import com.mathbeta.microedge.entity.*;
import com.mathbeta.microedge.enums.AppTaskResult;
import com.mathbeta.microedge.enums.AppTaskStatus;
import com.mathbeta.microedge.enums.AppTaskType;
import com.mathbeta.microedge.mapper.*;
import com.mathbeta.microedge.service.IAppTaskConfigService;
import com.mathbeta.microedge.service.IAppTaskDetailService;
import com.mathbeta.microedge.service.IAppTaskInfoService;
import com.mathbeta.microedge.utils.Errors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * 应用任务信息
 *
 * @author xuxiuyou
 * @date 2023/09/07
 */
@Slf4j
@Service
public class AppTaskInfoService extends BaseService<AppTaskInfo> implements IAppTaskInfoService {
    @Resource
    private AppTaskInfoMapper appTaskInfoMapper;
    @Resource(name = "runAction")
    private IAppAction<AppTaskDetail, Integer, CountDownLatch, Integer> runAction;
    @Resource(name = "removeAction")
    private IAppAction<AppTaskDetail, Integer, CountDownLatch, Integer> removeAction;
    @Resource(name = "restartAction")
    private IAppAction<AppTaskDetail, Integer, CountDownLatch, Integer> restartAction;
    @Resource(name = "upgradeAction")
    private IAppAction<AppUpgradeConfirmReq, Node, List<AppTaskConfig>, Integer> upgradeAction;
    @Resource
    private AppTaskDetailMapper appTaskDetailMapper;
    @Resource
    private IAppTaskDetailService appTaskDetailService;
    @Resource
    private AppMapper appMapper;
    @Resource
    private AppVersionMapper appVersionMapper;
    @Resource
    private AppTaskConfigMapper appTaskConfigMapper;
    @Resource
    private IAppTaskConfigService appTaskConfigService;
    @Resource
    private NodeAppMapper nodeAppMapper;
    @Resource
    private NodeMapper nodeMapper;
    /**
     * 各节点运维任务执行线程池
     */
    private ExecutorService pool = Executors.newFixedThreadPool(64);

    @Override
    protected AppTaskInfoMapper getMapper() {
        return appTaskInfoMapper;
    }

    private Result doAction(AppTaskInfoReq appTaskInfoReq,
                            AppTaskType appTaskType,
                            IAppAction<AppTaskDetail, Integer, CountDownLatch, Integer> detailAction) throws BusinessException {
        Map<String, Object> params = Maps.newHashMap();
        params.put("name", appTaskInfoReq.getAppName());
        List<App> apps = appMapper.query(params);
        if (CollectionUtils.isEmpty(apps)) {
            throw new RuntimeException(String.format("App not exists with name %s", appTaskInfoReq.getAppName()));
        }
        App app = apps.get(0);
        AppTaskInfo appTaskInfo = AppTaskInfo.builder()
                .type(appTaskType.getType())
                .status(AppTaskStatus.SUBMITTED.getStatus())
                .build();
        this.create(appTaskInfo);

        Object taskId = appTaskInfo.getId();
        pool.submit(() -> {
            appTaskInfo.setStatus(AppTaskStatus.EXECUTING.getStatus());
            appTaskInfoMapper.update(appTaskInfo);

            if (CollectionUtils.isNotEmpty(appTaskInfoReq.getNodeIds())) {
                CountDownLatch latch = new CountDownLatch(appTaskInfoReq.getNodeIds().size());
                List<Future<Integer>> statuses = Lists.newArrayList();
                appTaskInfoReq.getNodeIds().forEach(nodeId -> {
                    statuses.add(pool.submit(() -> {
                        AppTaskDetail appTaskDetail = AppTaskDetail.builder()
                                .taskId((String) taskId)
                                .appId((String) app.getId())
                                .versionId(appTaskInfoReq.getVersionId())
                                .nodeId(nodeId)
                                .status(AppTaskStatus.SUBMITTED.getStatus())
                                .build();
                        appTaskDetailService.create(appTaskDetail);
                        return detailAction.accept(appTaskDetail, appTaskInfoReq.getRetries(), latch);
                    }));
                });
                boolean success = true;
                try {
                    latch.await(10L, TimeUnit.MINUTES);
                    for (Future<Integer> f : statuses) {
                        Integer result = f.get(5, TimeUnit.SECONDS);
                        if (Objects.equals(AppTaskResult.FAIL.getResult(), result)) {
                            success = false;
                            break;
                        }
                    }
                } catch (Exception e) {
                    log.error("Failed to wait all host app tasks to end", e);
                } finally {
                    appTaskInfo.setStatus(AppTaskStatus.ENDED.getStatus());
                    appTaskInfo.setResult(success ? AppTaskResult.SUCCESS.getResult() : AppTaskResult.FAIL.getResult());
                    appTaskInfoMapper.update(appTaskInfo);
                }
            }
        });
        return Result.success(taskId);
    }

    @Override
    public Result run(AppTaskInfoReq appTaskInfoReq) throws BusinessException {
        log.info("Running app instance: {}", JSON.toJSONString(appTaskInfoReq));
        return doAction(appTaskInfoReq, AppTaskType.RUN, runAction);
    }

    @Override
    public Result restart(AppTaskInfoReq appTaskInfoReq) throws BusinessException {
        return doAction(appTaskInfoReq, AppTaskType.RESTART, restartAction);
    }

    /**
     * 查询运维任务在目标节点上的状态
     *
     * @param taskId
     * @return
     */
    @Override
    public Result status(String taskId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("taskId", taskId);
        List<AppTaskDetail> appTaskDetails = appTaskDetailMapper.query(params);

        return Result.result(ErrorInfo.SUCCESS, appTaskDetails);
    }

    @Override
    public Result remove(AppTaskInfoReq appTaskInfoReq) throws BusinessException {
        return doAction(appTaskInfoReq, AppTaskType.REMOVE, removeAction);
    }

    @Override
    public Result upgrade(AppUpgradeReq appUpgradeReq) throws BusinessException {
        AppTaskInfo appTaskInfo = AppTaskInfo.builder()
                .type(AppTaskType.UPGRADE.getType())
                .status(AppTaskStatus.SUBMITTED.getStatus())
                .build();
        this.create(appTaskInfo);

        Object taskId = appTaskInfo.getId();
        // 保存各应用版本顺序等配置
        List<String> versionIds = appUpgradeReq.getVersionIds();
        if (CollectionUtils.isNotEmpty(versionIds)) {
            for (int i = 0; i < versionIds.size(); i++) {
                AppVersion appVersion = appVersionMapper.queryById(versionIds.get(i));
                if (null == appVersion) {
                    log.error("No app version found for version id: {}", versionIds.get(i));
                    throw new RuntimeException(String.format("No app version found for version id: %s",
                            versionIds.get(i)));
                }
                AppTaskConfig appTaskConfig = AppTaskConfig.builder()
                        .taskId((String) taskId)
                        .appId(appVersion.getAppId())
                        .appVersionId(versionIds.get(i))
                        .order(i)
                        .build();
                appTaskConfigService.create(appTaskConfig);
            }
        }

        if (CollectionUtils.isNotEmpty(appUpgradeReq.getNodeIds())) {
            if (CollectionUtils.isNotEmpty(appUpgradeReq.getVersionIds())) {
                appUpgradeReq.getNodeIds().forEach(nodeId -> {
                    appUpgradeReq.getVersionIds().forEach(versionId -> {
                        AppVersion appVersion = appVersionMapper.queryById(versionId);
                        AppTaskDetail appTaskDetail = AppTaskDetail.builder()
                                .taskId((String) taskId)
                                .appId(appVersion.getAppId())
                                .nodeId(nodeId)
                                .versionId(versionId)
                                .status(AppTaskStatus.SUBMITTED.getStatus())
                                .build();
                        try {
                            appTaskDetailService.create(appTaskDetail);
                        } catch (BusinessException e) {
                            log.error("Failed to create app task detail info", e);
                        }
                    });
                });
            }
        }

        return Result.success(taskId);
    }

    @Override
    public Result startUpgrade(AppUpgradeConfirmReq appUpgradeConfirmReq) {
        try {
            pool.submit(() -> {
                Map<String, Object> params = Maps.newHashMap();
                params.put("taskId", appUpgradeConfirmReq.getUpgradeTaskId());
                List<AppTaskConfig> appTaskConfigs = appTaskConfigMapper.query(params);
                if (CollectionUtils.isNotEmpty(appTaskConfigs)) {
                    appTaskConfigs.sort(Comparator.comparing(AppTaskConfig::getOrder));
                }

                List<String> nodeIds = appUpgradeConfirmReq.getNodeIds();
                if (CollectionUtils.isNotEmpty(nodeIds)) {
                    AppTaskInfo appTaskInfo = appTaskInfoMapper.queryById(appUpgradeConfirmReq.getUpgradeTaskId());
                    appTaskInfo.setStatus(AppTaskStatus.EXECUTING.getStatus());
                    appTaskInfoMapper.update(appTaskInfo);

                    // 每个节点上执行升级
                    CountDownLatch latch = new CountDownLatch(nodeIds.size());
                    nodeIds.forEach(nodeId -> {
                        Node node = nodeMapper.queryById(nodeId);
                        if (null == node) {
                            log.error("Node not exists with id {}", nodeId);
                            throw new RuntimeException(String.format("Node not exists with id %s", nodeId));
                        }
                        // 提交至线程池
                        pool.submit(() -> {
                            try {
                                return upgradeAction.accept(appUpgradeConfirmReq, node,
                                        appTaskConfigs);
                            } catch (Exception e) {
                                log.error("Failed to upgrade app on node {}", nodeId, e);
                            } finally {
                                latch.countDown();
                            }
                            return AppTaskResult.FAIL.getResult();
                        });
                    });

                    try {
                        latch.await(10L, TimeUnit.MINUTES);
                    } catch (Exception e) {
                        log.error("Failed to wait all host app tasks to end", e);
                    } finally {
                        // 未执行和已失败总数
                        params.clear();
                        params.put("taskId", appUpgradeConfirmReq.getUpgradeTaskId());
                        params.put("result", null);
                        // 已提交但未执行
                        int submitted = appTaskDetailMapper.count(params);
                        params.put("result", AppTaskResult.FAIL.getResult());
                        // 已失败数
                        int failed = appTaskDetailMapper.count(params);
                        int nonSuccess = submitted + failed;
                        Integer result = null;
                        if (nonSuccess == 0 && failed == 0) {
                            result = AppTaskResult.SUCCESS.getResult();
                        }
                        if (failed > 0) {
                            result = AppTaskResult.FAIL.getResult();
                        }
                        if (null != result) {
                            appTaskInfo.setStatus(AppTaskStatus.ENDED.getStatus());
                            appTaskInfo.setResult(result);
                            appTaskInfoMapper.update(appTaskInfo);
                        }
                    }
                }
            });
        } catch (RejectedExecutionException e) {
            log.error("Failed to submit app upgrade task", e);
            return Result.fail(Errors.FAILED_TO_SUBMIT_APP_TASK.getCode(),
                    Errors.FAILED_TO_SUBMIT_APP_TASK.getMessage(), false);
        }

        return Result.success(true);
    }
}
