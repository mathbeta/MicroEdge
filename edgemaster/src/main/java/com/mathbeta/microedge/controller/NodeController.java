package com.mathbeta.microedge.controller;

import com.mathbeta.alphaboot.controller.BaseController;
import com.mathbeta.alphaboot.service.IService;
import com.mathbeta.microedge.entity.Node;
import com.mathbeta.microedge.service.INodeService;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
* 节点信息
*
* Created by xiuyou.xu on 2023/09/07.
*/
@Api(value = "节点信息", description = "节点信息"/*, authorizations = {@Authorization(value = "mesoauth", scopes = {@AuthorizationScope(scope = "node", description = "节点信息")})}*/)
@RestController
@RequestMapping("/node")
public class NodeController extends BaseController<Node> {
    @Resource
    private INodeService nodeService;

    @Override
    protected IService<Node> getService() {
        return nodeService;
    }
}
