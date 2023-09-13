package com.mathbeta.microedge.controller;

import com.mathbeta.alphaboot.controller.BaseController;
import com.mathbeta.microedge.entity.AppVersion;
import com.mathbeta.microedge.service.IAppVersionService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 应用版本
 *
 * @author xuxiuyou
 * @date 2023/09/07
 */
@Api(value = "应用版本", description = "应用版本")
@RestController
@RequestMapping("/appversion")
public class AppVersionController extends BaseController<AppVersion> {
    @Resource
    private IAppVersionService appVersionService;

    @Override
    protected IAppVersionService getService() {
        return appVersionService;
    }
}
