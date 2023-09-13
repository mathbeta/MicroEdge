package com.mathbeta.microedge.service.impl;


import com.mathbeta.alphaboot.service.impl.BaseService;
import com.mathbeta.microedge.entity.AppTaskConfig;
import com.mathbeta.microedge.mapper.AppTaskConfigMapper;
import com.mathbeta.microedge.service.IAppTaskConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 应用操作任务配置信息
 *
 * @author xuxiuyou
 * @date 2023/09/07
 */
@Slf4j
@Service
public class AppTaskConfigService extends BaseService<AppTaskConfig> implements IAppTaskConfigService {
    @Resource
    private AppTaskConfigMapper appTaskConfigMapper;

    @Override
    protected AppTaskConfigMapper getMapper() {
        return appTaskConfigMapper;
    }
}
