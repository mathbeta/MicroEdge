package com.mathbeta.microedge.service.impl;


import com.mathbeta.alphaboot.service.impl.BaseService;
import com.mathbeta.microedge.entity.AppTaskDetail;
import com.mathbeta.microedge.mapper.AppTaskDetailMapper;
import com.mathbeta.microedge.service.IAppTaskDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 运维任务明细信息
 *
 * @author xuxiuyou
 * @date 2023/09/07
 */
@Slf4j
@Service
public class AppTaskDetailService extends BaseService<AppTaskDetail, AppTaskDetailMapper> implements IAppTaskDetailService {
    @Resource
    private AppTaskDetailMapper appTaskDetailMapper;

    @Override
    protected AppTaskDetailMapper getMapper() {
        return appTaskDetailMapper;
    }
}
