package com.mathbeta.microedge.controller;

import com.mathbeta.alphaboot.controller.BaseController;
import com.mathbeta.alphaboot.service.IService;
import com.mathbeta.microedge.entity.App;
import com.mathbeta.microedge.service.IAppService;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
* 应用信息
*
* Created by xiuyou.xu on 2023/09/07.
*/
@Api(value = "应用信息", description = "应用信息"/*, authorizations = {@Authorization(value = "mesoauth", scopes = {@AuthorizationScope(scope = "app", description = "应用信息")})}*/)
@RestController
@RequestMapping("/app")
public class AppController extends BaseController<App> {
    @Resource
    private IAppService appService;

    @Override
    protected IService<App> getService() {
        return appService;
    }
}
