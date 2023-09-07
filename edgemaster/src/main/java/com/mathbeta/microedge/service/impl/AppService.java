package com.mathbeta.microedge.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mathbeta.alphaboot.mapper.IMapper;
import com.mathbeta.alphaboot.service.impl.BaseService;
import com.mathbeta.microedge.entity.App;
import com.mathbeta.microedge.mapper.AppMapper;
import com.mathbeta.microedge.service.IAppService;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * 应用信息
 * Created by xiuyou.xu on 2023/09/07.
 */
@Slf4j
@Service
public class AppService extends BaseService<App> implements IAppService {
    // use lombok log
    // private static Logger log = LoggerFactory.getLogger(AppService.class);

    @Resource
    private AppMapper appMapper;

    @Override
    protected IMapper<App> getMapper() {
        return appMapper;
    }
}
