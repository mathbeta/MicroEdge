package com.mathbeta.microedge.service;


import com.mathbeta.alphaboot.entity.Result;
import com.mathbeta.alphaboot.exception.BusinessException;
import com.mathbeta.alphaboot.service.IService;
import com.mathbeta.microedge.entity.Node;
import com.mathbeta.microedge.entity.NodeRegister;
import org.springframework.stereotype.Service;

/**
 * 节点信息
 *
 * @author xuxiuyou
 * @date 2023/09/07
 */
@Service
public interface INodeService extends IService<Node> {
    /**
     * 注册agent节点
     *
     * @param nodeRegister
     * @return
     */
    Result register(NodeRegister nodeRegister) throws BusinessException;

    /**
     * 列出在线节点
     *
     * @return
     */
    Result listOnline();
}
