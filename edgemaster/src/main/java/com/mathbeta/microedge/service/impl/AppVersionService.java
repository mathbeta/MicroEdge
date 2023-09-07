package com.mathbeta.microedge.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mathbeta.alphaboot.mapper.IMapper;
import com.mathbeta.alphaboot.service.impl.BaseService;
import com.mathbeta.microedge.entity.AppVersion;
import com.mathbeta.microedge.mapper.AppVersionMapper;
import com.mathbeta.microedge.service.IAppVersionService;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * 应用版本
 * Created by xiuyou.xu on 2023/09/07.
 */
@Slf4j
@Service
public class AppVersionService extends BaseService<AppVersion> implements IAppVersionService {
    // use lombok log
    // private static Logger log = LoggerFactory.getLogger(AppVersionService.class);

    @Resource
    private AppVersionMapper appVersionMapper;

    @Override
    protected IMapper<AppVersion> getMapper() {
        return appVersionMapper;
    }
}
