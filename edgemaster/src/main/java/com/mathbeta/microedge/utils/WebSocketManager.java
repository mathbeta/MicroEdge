package com.mathbeta.microedge.utils;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.jsr356.JsrSession;

import javax.websocket.Session;
import java.util.Map;
import java.util.Set;

/**
 * websocket客户端管理工具类
 *
 * @author xuxiuyou
 * @date 2023/9/11 13:53
 */
@Slf4j
public class WebSocketManager {
    private static WebSocketManager manager = new WebSocketManager();
    /**
     * websocket客户端map：agentId => Session
     */
    private Map<String, Session> sessionMap = Maps.newConcurrentMap();
    /**
     * sessionId => agentId
     */
    private Map<String, String> sessionIdMap = Maps.newConcurrentMap();

    private WebSocketManager() {
    }

    public static WebSocketManager getManager() {
        return manager;
    }

    /**
     * 添加session
     *
     * @param agentId
     * @param session
     */
    public void addClient(String agentId, Session session) {
        sessionMap.put(agentId, session);
        sessionIdMap.put(session.getId(), agentId);
    }

    /**
     * 删除session
     *
     * @param session
     */
    public void removeClient(Session session) {
        String sessionId = session.getId();
        String agentId = sessionIdMap.remove(sessionId);
        if (null != agentId) {
            sessionMap.remove(agentId);
        }
    }

    /**
     * 向指定agent发送消息
     *
     * @param agentId
     * @param msg
     */
    public void sendMsg(String agentId, String msg) {
        Session session = sessionMap.get(agentId);
        if (null != session) {
            log.debug("Sending message {} to client {}", msg,
                    ((JsrSession) session).getWebSocketSession().getRemoteAddress());
            session.getAsyncRemote().sendText(msg);
        }
    }

    public Set<String> agentIds() {
        return sessionMap.keySet();
    }
}
