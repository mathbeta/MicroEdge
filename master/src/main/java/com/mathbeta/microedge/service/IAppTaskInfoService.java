package com.mathbeta.microedge.service;


import com.mathbeta.alphaboot.entity.Result;
import com.mathbeta.alphaboot.exception.BusinessException;
import com.mathbeta.alphaboot.service.IBaseService;
import com.mathbeta.microedge.entity.AppTaskInfo;
import com.mathbeta.microedge.entity.AppTaskInfoReq;
import com.mathbeta.microedge.entity.AppUpgradeConfirmReq;
import com.mathbeta.microedge.entity.AppUpgradeReq;
import org.springframework.stereotype.Service;

/**
 * 应用任务信息
 *
 * @author xuxiuyou
 * @date 2023/09/07
 */
@Service
public interface IAppTaskInfoService extends IBaseService<AppTaskInfo> {

    /**
     * 查询应用任务状态
     *
     * @param taskId
     * @return
     */
    Result status(String taskId);

    /**
     * 部署应用
     *
     * @param appTaskInfoReq
     * @return
     */
    Result run(AppTaskInfoReq appTaskInfoReq) throws BusinessException;

    /**
     * 重启应用
     *
     * @param appTaskInfoReq
     * @return
     */
    Result restart(AppTaskInfoReq appTaskInfoReq) throws BusinessException;

    /**
     * 删除应用
     *
     * @param appTaskInfoReq
     * @return
     */
    Result remove(AppTaskInfoReq appTaskInfoReq) throws BusinessException;

    /**
     * 升级应用
     *
     * @param appUpgradeReq
     * @return
     */
    Result upgrade(AppUpgradeReq appUpgradeReq) throws BusinessException;

    /**
     * 开始升级应用，触发升级任务的执行
     *
     * @param appUpgradeConfirmReqDTO
     * @return
     */
    Result startUpgrade(AppUpgradeConfirmReq appUpgradeConfirmReqDTO);
}
