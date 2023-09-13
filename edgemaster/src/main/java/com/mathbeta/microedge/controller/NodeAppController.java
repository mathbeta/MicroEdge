package com.mathbeta.microedge.controller;

import com.mathbeta.alphaboot.controller.BaseController;
import com.mathbeta.microedge.entity.NodeApp;
import com.mathbeta.microedge.service.INodeAppService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 节点应用关联表
 *
 * @author xuxiuyou
 * @date 2023/09/07
 */
@Api(value = "节点应用关联表", description = "节点应用关联表")
@RestController
@RequestMapping("/nodeapp")
public class NodeAppController extends BaseController<NodeApp> {
    @Resource
    private INodeAppService nodeAppService;

    @Override
    protected INodeAppService getService() {
        return nodeAppService;
    }
}
