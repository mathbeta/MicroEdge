package com.mathbeta.microedge.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mathbeta.alphaboot.mapper.IMapper;
import com.mathbeta.alphaboot.service.impl.BaseService;
import com.mathbeta.microedge.entity.AppTaskConfig;
import com.mathbeta.microedge.mapper.AppTaskConfigMapper;
import com.mathbeta.microedge.service.IAppTaskConfigService;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * 应用操作任务配置信息
 * Created by xiuyou.xu on 2023/09/07.
 */
@Slf4j
@Service
public class AppTaskConfigService extends BaseService<AppTaskConfig> implements IAppTaskConfigService {
    // use lombok log
    // private static Logger log = LoggerFactory.getLogger(AppTaskConfigService.class);

    @Resource
    private AppTaskConfigMapper appTaskConfigMapper;

    @Override
    protected IMapper<AppTaskConfig> getMapper() {
        return appTaskConfigMapper;
    }
}
