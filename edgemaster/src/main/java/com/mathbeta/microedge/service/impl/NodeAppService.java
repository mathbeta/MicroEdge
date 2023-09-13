package com.mathbeta.microedge.service.impl;


import com.mathbeta.alphaboot.service.impl.BaseService;
import com.mathbeta.microedge.entity.NodeApp;
import com.mathbeta.microedge.mapper.NodeAppMapper;
import com.mathbeta.microedge.service.INodeAppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 节点应用关联表
 *
 * @author xuxiuyou
 * @date 2023/09/07
 */
@Slf4j
@Service
public class NodeAppService extends BaseService<NodeApp> implements INodeAppService {
    @Resource
    private NodeAppMapper nodeAppMapper;

    @Override
    protected NodeAppMapper getMapper() {
        return nodeAppMapper;
    }
}
