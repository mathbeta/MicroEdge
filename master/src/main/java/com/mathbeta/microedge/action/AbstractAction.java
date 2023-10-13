package com.mathbeta.microedge.action;


import com.mathbeta.microedge.entity.AppTaskDetail;
import com.mathbeta.microedge.mapper.*;
import com.mathbeta.microedge.service.IAppService;
import com.mathbeta.microedge.service.INodeAppService;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

/**
 * 抽象运维动作
 *
 * @author xuxiuyou
 * @date 2023/8/12 14:08
 */
public abstract class AbstractAction implements IAppAction<AppTaskDetail, Integer, CountDownLatch, Integer> {
    @Resource
    protected AppTaskDetailMapper appTaskDetailMapper;

    @Resource
    protected AppMapper appMapper;

    @Resource
    protected AppVersionMapper appVersionMapper;

    @Resource
    protected NodeAppMapper nodeAppMapper;

    @Resource
    protected NodeMapper nodeMapper;

    @Resource
    protected INodeAppService nodeAppService;

    @Resource
    protected IAppService appService;
}
