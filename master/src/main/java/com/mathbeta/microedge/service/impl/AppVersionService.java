package com.mathbeta.microedge.service.impl;


import com.mathbeta.alphaboot.service.impl.BaseService;
import com.mathbeta.microedge.entity.AppVersion;
import com.mathbeta.microedge.mapper.AppVersionMapper;
import com.mathbeta.microedge.service.IAppVersionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 应用版本
 *
 * @author xuxiuyou
 * @date 2023/09/07
 */
@Slf4j
@Service
public class AppVersionService extends BaseService<AppVersion, AppVersionMapper> implements IAppVersionService {
    @Resource
    private AppVersionMapper appVersionMapper;

    @Override
    protected AppVersionMapper getMapper() {
        return appVersionMapper;
    }
}
