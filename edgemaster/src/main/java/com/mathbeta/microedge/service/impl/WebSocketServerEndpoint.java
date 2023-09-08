package com.mathbeta.microedge.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

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
    }

    @OnMessage
    public void onMessage(String msg, Session session) {
        log.info("Client {} send message {}", session.getId(), msg);
        session.getAsyncRemote().sendText(msg);
    }

    @OnError
    public void onError(Session session, Throwable t) {
        log.error("Client {} error", session.getId(), t);
        try {
            session.close();
        } catch (IOException e) {
            log.error("Failed to close client {} in OnError", session.getId(), e);
        }
    }
}
