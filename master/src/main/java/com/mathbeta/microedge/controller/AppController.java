package com.mathbeta.microedge.controller;

import com.mathbeta.alphaboot.controller.BaseController;
import com.mathbeta.alphaboot.entity.Result;
import com.mathbeta.microedge.entity.App;
import com.mathbeta.microedge.service.IAppService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 应用信息
 * <p>
 *
 * @author xuxiuyou
 * @date 2023/09/07
 */
@Api(value = "应用信息", description = "应用信息")
@RestController
@RequestMapping("/app")
public class AppController extends BaseController<App, IAppService> {
    @Resource
    private IAppService appService;

    @Override
    protected IAppService getService() {
        return appService;
    }


    @GetMapping(path = "listApps", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "查询节点应用列表", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result listApps(@ApiParam(name = "nodeId") @RequestParam(value = "nodeId", required = false) String nodeId) {
        return appService.listApps(nodeId);
    }
}
