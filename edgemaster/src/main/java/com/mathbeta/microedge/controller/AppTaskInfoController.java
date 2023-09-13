package com.mathbeta.microedge.controller;

import com.mathbeta.alphaboot.controller.BaseController;
import com.mathbeta.alphaboot.entity.Result;
import com.mathbeta.alphaboot.exception.BusinessException;
import com.mathbeta.microedge.entity.AppTaskInfo;
import com.mathbeta.microedge.entity.AppTaskInfoReq;
import com.mathbeta.microedge.entity.AppUpgradeConfirmReq;
import com.mathbeta.microedge.entity.AppUpgradeReq;
import com.mathbeta.microedge.service.IAppTaskInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 应用任务信息
 * <p>
 *
 * @author xuxiuyou
 * @date 2023/09/07
 */
@Api(value = "应用任务信息", description = "应用任务信息")
@RestController
@RequestMapping("/apptaskinfo")
public class AppTaskInfoController extends BaseController<AppTaskInfo> {
    @Resource
    private IAppTaskInfoService appTaskInfoService;

    @Override
    protected IAppTaskInfoService getService() {
        return appTaskInfoService;
    }


    @GetMapping(path = "status/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "查询应用任务状态", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result status(@ApiParam(name = "应用任务id") @PathVariable("id") String id) {
        return appTaskInfoService.status(id);
    }

    @PostMapping(path = "run", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "部署应用", httpMethod = "POST",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result run(@ApiParam(name = "部署应用请求参数") @RequestBody AppTaskInfoReq req) throws BusinessException {
        return appTaskInfoService.run(req);
    }

    @PostMapping(path = "restart", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "重启应用", httpMethod = "POST",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result restart(@ApiParam(name = "重启应用请求参数") @RequestBody AppTaskInfoReq req) throws BusinessException {
        return appTaskInfoService.restart(req);
    }

    @PostMapping(path = "remove", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除应用", httpMethod = "POST",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result remove(@ApiParam(name = "删除应用请求参数") @RequestBody AppTaskInfoReq req) throws BusinessException {
        return appTaskInfoService.remove(req);
    }

    @PostMapping(path = "upgrade", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "升级应用", httpMethod = "POST",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result upgrade(@ApiParam(name = "升级应用请求参数") @RequestBody AppUpgradeReq req) throws BusinessException {
        return appTaskInfoService.upgrade(req);
    }

    @PostMapping(path = "startUpgrade", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "开始执行升级", httpMethod = "POST",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result startUpgrade(@ApiParam(name = "开始升级应用请求参数") @RequestBody AppUpgradeConfirmReq req) {
        return appTaskInfoService.startUpgrade(req);
    }
}
