package com.mathbeta.microedge.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mathbeta.alphaboot.mapper.IMapper;
import com.mathbeta.alphaboot.service.impl.BaseService;
import com.mathbeta.microedge.entity.Node;
import com.mathbeta.microedge.mapper.NodeMapper;
import com.mathbeta.microedge.service.INodeService;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * 节点信息
 * Created by xiuyou.xu on 2023/09/07.
 */
@Slf4j
@Service
public class NodeService extends BaseService<Node> implements INodeService {
    // use lombok log
    // private static Logger log = LoggerFactory.getLogger(NodeService.class);

    @Resource
    private NodeMapper nodeMapper;

    @Override
    protected IMapper<Node> getMapper() {
        return nodeMapper;
    }
}
