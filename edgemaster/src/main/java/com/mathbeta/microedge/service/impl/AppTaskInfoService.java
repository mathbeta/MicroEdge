package com.mathbeta.microedge.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mathbeta.alphaboot.mapper.IMapper;
import com.mathbeta.alphaboot.service.impl.BaseService;
import com.mathbeta.microedge.entity.AppTaskInfo;
import com.mathbeta.microedge.mapper.AppTaskInfoMapper;
import com.mathbeta.microedge.service.IAppTaskInfoService;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * 主机应用运维任务信息
 * Created by xiuyou.xu on 2023/09/07.
 */
@Slf4j
@Service
public class AppTaskInfoService extends BaseService<AppTaskInfo> implements IAppTaskInfoService {
    // use lombok log
    // private static Logger log = LoggerFactory.getLogger(AppTaskInfoService.class);

    @Resource
    private AppTaskInfoMapper appTaskInfoMapper;

    @Override
    protected IMapper<AppTaskInfo> getMapper() {
        return appTaskInfoMapper;
    }
}
