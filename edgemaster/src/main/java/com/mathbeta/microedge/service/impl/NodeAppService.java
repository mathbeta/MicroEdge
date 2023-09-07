package com.mathbeta.microedge.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mathbeta.alphaboot.mapper.IMapper;
import com.mathbeta.alphaboot.service.impl.BaseService;
import com.mathbeta.microedge.entity.NodeApp;
import com.mathbeta.microedge.mapper.NodeAppMapper;
import com.mathbeta.microedge.service.INodeAppService;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * 节点应用关联表
 * Created by xiuyou.xu on 2023/09/07.
 */
@Slf4j
@Service
public class NodeAppService extends BaseService<NodeApp> implements INodeAppService {
    // use lombok log
    // private static Logger log = LoggerFactory.getLogger(NodeAppService.class);

    @Resource
    private NodeAppMapper nodeAppMapper;

    @Override
    protected IMapper<NodeApp> getMapper() {
        return nodeAppMapper;
    }
}
