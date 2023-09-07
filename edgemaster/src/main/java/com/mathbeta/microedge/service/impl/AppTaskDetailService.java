package com.mathbeta.microedge.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mathbeta.alphaboot.mapper.IMapper;
import com.mathbeta.alphaboot.service.impl.BaseService;
import com.mathbeta.microedge.entity.AppTaskDetail;
import com.mathbeta.microedge.mapper.AppTaskDetailMapper;
import com.mathbeta.microedge.service.IAppTaskDetailService;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * 运维任务明细信息
 * Created by xiuyou.xu on 2023/09/07.
 */
@Slf4j
@Service
public class AppTaskDetailService extends BaseService<AppTaskDetail> implements IAppTaskDetailService {
    // use lombok log
    // private static Logger log = LoggerFactory.getLogger(AppTaskDetailService.class);

    @Resource
    private AppTaskDetailMapper appTaskDetailMapper;

    @Override
    protected IMapper<AppTaskDetail> getMapper() {
        return appTaskDetailMapper;
    }
}
