package com.mathbeta.microedge.service.impl;

import com.alibaba.fastjson.JSON;
import com.mathbeta.microedge.entity.AppInstance;
import com.mathbeta.microedge.entity.BaseMsg;
import com.mathbeta.microedge.entity.HeartbeatMsg;
import com.mathbeta.microedge.entity.IdMsg;
import com.mathbeta.microedge.utils.AppManager;
import com.mathbeta.microedge.utils.WebSocketManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.jetty.websocket.jsr356.JsrSession;
import org.springframework.stereotype.Component;

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
@ServerEndpoint("/ws")
public class WebSocketServerEndpoint {
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

                case BaseMsg.TYPE_HEARTBEAT:
                    session.getAsyncRemote().sendText(JSON.toJSONString(HeartbeatMsg.builder()
                            .type(BaseMsg.TYPE_HEARTBEAT)
                            .build()));
                    HeartbeatMsg heartbeatMsg = JSON.parseObject(msg, HeartbeatMsg.class);
                    syncAppStatus(heartbeatMsg);
                    break;

                default:
                    log.warn("Unknown message type {} of msg {}", baseMsg.getType(), msg);
            }
        }
    }

    /**
     * 同步节点上的应用信息
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
