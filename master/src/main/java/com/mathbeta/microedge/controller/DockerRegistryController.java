package com.mathbeta.microedge.controller;

import com.mathbeta.alphaboot.controller.BaseController;
import com.mathbeta.microedge.entity.DockerRegistry;
import com.mathbeta.microedge.service.IDockerRegistryService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * docker镜像仓库
 *
 * @author xuxiuyou
 * @date 2023/09/18
 */
@Api(value = "docker镜像仓库", description = "docker镜像仓库")
@RestController
@RequestMapping("/dockerregistry")
public class DockerRegistryController extends BaseController<DockerRegistry, IDockerRegistryService> {
    @Resource
    private IDockerRegistryService dockerRegistryService;

    @Override
    protected IDockerRegistryService getService() {
        return dockerRegistryService;
    }
}
