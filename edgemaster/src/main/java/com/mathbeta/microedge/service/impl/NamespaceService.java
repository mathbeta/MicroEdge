package com.mathbeta.microedge.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mathbeta.alphaboot.mapper.IMapper;
import com.mathbeta.alphaboot.service.impl.BaseService;
import com.mathbeta.microedge.entity.Namespace;
import com.mathbeta.microedge.mapper.NamespaceMapper;
import com.mathbeta.microedge.service.INamespaceService;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * 命名空间
 * Created by xiuyou.xu on 2023/09/07.
 */
@Slf4j
@Service
public class NamespaceService extends BaseService<Namespace> implements INamespaceService {
    // use lombok log
    // private static Logger log = LoggerFactory.getLogger(NamespaceService.class);

    @Resource
    private NamespaceMapper namespaceMapper;

    @Override
    protected IMapper<Namespace> getMapper() {
        return namespaceMapper;
    }
}
