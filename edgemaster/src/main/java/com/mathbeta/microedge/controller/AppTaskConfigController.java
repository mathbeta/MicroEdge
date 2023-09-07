package com.mathbeta.microedge.controller;

import com.mathbeta.alphaboot.controller.BaseController;
import com.mathbeta.alphaboot.service.IService;
import com.mathbeta.microedge.entity.AppTaskConfig;
import com.mathbeta.microedge.service.IAppTaskConfigService;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
* 应用操作任务配置信息
*
* Created by xiuyou.xu on 2023/09/07.
*/
@Api(value = "应用操作任务配置信息", description = "应用操作任务配置信息"/*, authorizations = {@Authorization(value = "mesoauth", scopes = {@AuthorizationScope(scope = "apptaskconfig", description = "应用操作任务配置信息")})}*/)
@RestController
@RequestMapping("/apptaskconfig")
public class AppTaskConfigController extends BaseController<AppTaskConfig> {
    @Resource
    private IAppTaskConfigService appTaskConfigService;

    @Override
    protected IService<AppTaskConfig> getService() {
        return appTaskConfigService;
    }
}
