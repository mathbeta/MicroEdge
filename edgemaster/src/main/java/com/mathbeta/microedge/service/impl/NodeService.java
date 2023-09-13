package com.mathbeta.microedge.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.mathbeta.alphaboot.entity.ErrorInfo;
import com.mathbeta.alphaboot.entity.Result;
import com.mathbeta.alphaboot.exception.BusinessException;
import com.mathbeta.alphaboot.service.impl.BaseService;
import com.mathbeta.alphaboot.utils.TableUtil;
import com.mathbeta.microedge.entity.Namespace;
import com.mathbeta.microedge.entity.Node;
import com.mathbeta.microedge.entity.NodeRegister;
import com.mathbeta.microedge.mapper.NamespaceMapper;
import com.mathbeta.microedge.mapper.NodeMapper;
import com.mathbeta.microedge.service.INodeService;
import com.mathbeta.microedge.utils.Errors;
import com.mathbeta.microedge.utils.WebSocketManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 节点信息
 *
 * @author xuxiuyou
 * @date 2023/09/07
 */
@Slf4j
@Service
public class NodeService extends BaseService<Node> implements INodeService {
    @Resource
    private NodeMapper nodeMapper;

    @Resource
    private NamespaceMapper namespaceMapper;

    @Override
    protected NodeMapper getMapper() {
        return nodeMapper;
    }

    @Override
    public Result register(NodeRegister nodeRegister) throws BusinessException {
        Map<String, Object> map = Maps.newHashMap();
        map.put("name", nodeRegister.getNamespace());
        List<Namespace> namespaces = namespaceMapper.query(map);
        if (CollectionUtils.isNotEmpty(namespaces)) {
            Node node = Node.builder()
                    .name(String.format("%s_%s",
                            nodeRegister.getNamespace(),
                            nodeRegister.getIp()))
                    .ip(nodeRegister.getIp())
                    .nsId((String) namespaces.get(0).getId())
                    .token(TableUtil.uuid())
                    .build();
            this.create(node);

            JSONObject jo = new JSONObject();
            jo.put("id", node.getId());
            jo.put("token", node.getToken());
            return Result.result(ErrorInfo.SUCCESS, jo);
        }
        return Result.fail(Errors.NAMESPACE_NOT_EXISTS.getCode(),
                Errors.NAMESPACE_NOT_EXISTS.getMessage(),
                String.format("namespace %s not exits", nodeRegister.getNamespace()));
    }

    @Override
    public Result listOnline() {
        return Result.success(WebSocketManager.getManager()
                .agentIds().stream().map(id -> nodeMapper.queryById(id))
                .collect(Collectors.toList()));
    }
}
