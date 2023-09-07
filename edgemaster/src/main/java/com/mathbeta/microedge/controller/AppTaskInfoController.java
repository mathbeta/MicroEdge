package com.mathbeta.microedge.controller;

import com.mathbeta.alphaboot.controller.BaseController;
import com.mathbeta.alphaboot.service.IService;
import com.mathbeta.microedge.entity.AppTaskInfo;
import com.mathbeta.microedge.service.IAppTaskInfoService;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
* 主机应用运维任务信息
*
* Created by xiuyou.xu on 2023/09/07.
*/
@Api(value = "主机应用运维任务信息", description = "主机应用运维任务信息"/*, authorizations = {@Authorization(value = "mesoauth", scopes = {@AuthorizationScope(scope = "apptaskinfo", description = "主机应用运维任务信息")})}*/)
@RestController
@RequestMapping("/apptaskinfo")
public class AppTaskInfoController extends BaseController<AppTaskInfo> {
    @Resource
    private IAppTaskInfoService appTaskInfoService;

    @Override
    protected IService<AppTaskInfo> getService() {
        return appTaskInfoService;
    }
}
