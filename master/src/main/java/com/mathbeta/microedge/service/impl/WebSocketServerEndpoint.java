package com.mathbeta.microedge.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.mathbeta.alphaboot.exception.BusinessException;
import com.mathbeta.alphaboot.utils.ParamBuilder;
import com.mathbeta.microedge.entity.*;
import com.mathbeta.microedge.service.IDockerRegistryService;
import com.mathbeta.microedge.utils.AppManager;
import com.mathbeta.microedge.utils.WebSocketManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.jetty.websocket.jsr356.JsrSession;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.SpringConfigurator;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * 向边缘节点提供的websocket服务接口
 *
 * @author xuxiuyou
 * @date 2023/9/8 11:39
 */
@Slf4j
@Component
@ServerEndpoint(value = "/ws")
public class WebSocketServerEndpoint {
    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        WebSocketServerEndpoint.applicationContext = applicationContext;
    }

    @OnOpen
    public void onOpen(Session session) {
        log.info("Client {} connected", session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        log.info("Client {} closed", session.getId());
        agentOffline(session);
    }

    @OnMessage
    public void onMessage(String msg, Session session) {
        JsrSession js = (JsrSession) session;
        log.info("Received msg {} from {}", msg, js.getWebSocketSession().getRemoteAddress());
        BaseMsg baseMsg = JSON.parseObject(msg, BaseMsg.class);
        if (null != baseMsg) {
            switch (baseMsg.getType()) {
                case BaseMsg.TYPE_ID:
                    agentOnline(msg, session);
                    break;

                case BaseMsg.TYPE_REGISTRIES_REQ:
                    queryRegistries(msg, session);
                    break;

                case BaseMsg.TYPE_HEARTBEAT:
                    session.getAsyncRemote().sendText(HeartbeatMsg.builder()
                            .type(BaseMsg.TYPE_HEARTBEAT)
                            .build().toJsonString());
                    HeartbeatMsg heartbeatMsg = JSON.parseObject(msg, HeartbeatMsg.class);
                    syncAppStatus(heartbeatMsg);
                    break;

                default:
                    log.warn("Unknown message type {} of msg {}", baseMsg.getType(), msg);
            }
        }
    }

    private void queryRegistries(String msg, Session session) {
//        RegistryReqMsg registryReqMsg=JSON.parseObject(msg, RegistryReqMsg.class);
        List<DockerRegistry> dockerRegistries = null;
        try {
            IDockerRegistryService dockerRegistryService = applicationContext.getBean(IDockerRegistryService.class);
            dockerRegistries = dockerRegistryService.query(ParamBuilder.builder().build());
        } catch (BusinessException e) {
            log.error("Failed to query docker registries", e);
        }
        List<Registry> registries = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(dockerRegistries)) {
            dockerRegistries.forEach(r -> {
                registries.add(Registry.builder()
                        .id(String.valueOf(r.getId()))
                        .url(r.getUrl())
                        .userEmail(r.getUserEmail())
                        .userName(r.getUserName())
                        .password(r.getPassword())
                        .isPublic(Objects.equals(r.getIsPublic(), 1))
                        .build());
            });
        }

        session.getAsyncRemote().sendText(RegistryRespMsg.builder()
                .type(BaseMsg.TYPE_REGISTRIES_RESP)
                .registries(registries)
                .build().toJsonString());
    }

    /**
     * 同步节点上的应用实例信息
     *
     * @param heartbeatMsg
     */
    private void syncAppStatus(HeartbeatMsg heartbeatMsg) {
        List<AppInstance> instances = AppManager.getManager().listInstances();
        String agentId = heartbeatMsg.getAgentId();
        instances.forEach(instance -> {
            if (Objects.equals(instance.getNodeId(), agentId)) {
                AppManager.getManager().removeInstance(instance.getContainerName());
            }
        });
        List<AppInstance> list = heartbeatMsg.getAppInstances();
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(instance -> {
                AppManager.getManager().addInstance(instance.getContainerName(), instance);
            });
        }
    }

    /**
     * agent上线
     *
     * @param msg
     * @param session
     */
    private void agentOnline(String msg, Session session) {
        IdMsg idMsg = JSON.parseObject(msg, IdMsg.class);
        WebSocketManager.getManager().addClient(idMsg.getAgentId(), session);
    }

    /**
     * agent下线
     *
     * @param session
     */
    private void agentOffline(Session session) {
        WebSocketManager.getManager().removeClient(session);
    }

    @OnError
    public void onError(Session session, Throwable t) {
        log.error("Client {} error", session.getId(), t);
        try {
            session.close();
            agentOffline(session);
        } catch (IOException e) {
            log.error("Failed to close client {} in OnError", session.getId(), e);
        }
    }
}
