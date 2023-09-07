package com.mathbeta.microedge.controller;

import com.mathbeta.alphaboot.controller.BaseController;
import com.mathbeta.alphaboot.service.IService;
import com.mathbeta.microedge.entity.Namespace;
import com.mathbeta.microedge.service.INamespaceService;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
* 命名空间
*
* Created by xiuyou.xu on 2023/09/07.
*/
@Api(value = "命名空间", description = "命名空间"/*, authorizations = {@Authorization(value = "mesoauth", scopes = {@AuthorizationScope(scope = "namespace", description = "命名空间")})}*/)
@RestController
@RequestMapping("/namespace")
public class NamespaceController extends BaseController<Namespace> {
    @Resource
    private INamespaceService namespaceService;

    @Override
    protected IService<Namespace> getService() {
        return namespaceService;
    }
}
