package com.mathbeta.microedge.controller;

import com.mathbeta.alphaboot.controller.BaseController;
import com.mathbeta.alphaboot.service.IService;
import com.mathbeta.microedge.entity.AppTaskDetail;
import com.mathbeta.microedge.service.IAppTaskDetailService;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
* 运维任务明细信息
*
* Created by xiuyou.xu on 2023/09/07.
*/
@Api(value = "运维任务明细信息", description = "运维任务明细信息"/*, authorizations = {@Authorization(value = "mesoauth", scopes = {@AuthorizationScope(scope = "apptaskdetail", description = "运维任务明细信息")})}*/)
@RestController
@RequestMapping("/apptaskdetail")
public class AppTaskDetailController extends BaseController<AppTaskDetail> {
    @Resource
    private IAppTaskDetailService appTaskDetailService;

    @Override
    protected IService<AppTaskDetail> getService() {
        return appTaskDetailService;
    }
}
