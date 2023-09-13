package com.mathbeta.microedge.service.impl;


import com.mathbeta.alphaboot.service.impl.BaseService;
import com.mathbeta.microedge.entity.Namespace;
import com.mathbeta.microedge.mapper.NamespaceMapper;
import com.mathbeta.microedge.service.INamespaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 命名空间
 *
 * @author xuxiuyou
 * @date 2023/09/07
 */
@Slf4j
@Service
public class NamespaceService extends BaseService<Namespace> implements INamespaceService {
    @Resource
    private NamespaceMapper namespaceMapper;

    @Override
    protected NamespaceMapper getMapper() {
        return namespaceMapper;
    }
}
