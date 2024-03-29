package com.mathbeta.microedge.service.impl;


import com.mathbeta.alphaboot.service.impl.BaseService;
import com.mathbeta.microedge.entity.DockerRegistry;
import com.mathbeta.microedge.mapper.DockerRegistryMapper;
import com.mathbeta.microedge.service.IDockerRegistryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * docker镜像仓库
 *
 * @author xuxiuyou
 * @date 2023/09/18
 */
@Slf4j
@Service
public class DockerRegistryService extends BaseService<DockerRegistry, DockerRegistryMapper> implements IDockerRegistryService {
    @Resource
    private DockerRegistryMapper dockerRegistryMapper;

    @Override
    protected DockerRegistryMapper getMapper() {
        return dockerRegistryMapper;
    }
}
