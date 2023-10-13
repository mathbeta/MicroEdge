package com.mathbeta.microedge.controller;

import com.mathbeta.alphaboot.controller.BaseController;
import com.mathbeta.alphaboot.entity.Result;
import com.mathbeta.alphaboot.exception.BusinessException;
import com.mathbeta.microedge.entity.Node;
import com.mathbeta.microedge.entity.NodeRegister;
import com.mathbeta.microedge.service.INodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 节点信息
 *
 * @author xuxiuyou
 * @date 2023/09/07
 */
@Api(value = "节点信息", description = "节点信息")
@RestController
@RequestMapping("/node")
public class NodeController extends BaseController<Node, INodeService> {
    @Resource
    private INodeService nodeService;

    @Override
    protected INodeService getService() {
        return nodeService;
    }

    @RequestMapping(path = "register", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "/register",
            notes = "注册节点",
            response = Result.class,
            consumes = "application/json",
            produces = "application/json"
    )
    @ResponseBody
    public Result register(@RequestBody NodeRegister nodeRegister) throws BusinessException {
        return this.getService().register(nodeRegister);
    }

    @RequestMapping(path = "listOnline", method = RequestMethod.GET)
    @ApiOperation(
            value = "列出在线节点",
            notes = "列出在线节点",
            response = Result.class,
            produces = "application/json"
    )
    @ResponseBody
    public Result listOnline() throws BusinessException {
        return this.getService().listOnline();
    }
}
