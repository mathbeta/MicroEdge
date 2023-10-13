package com.mathbeta.microedge.utils;

import com.alibaba.fastjson.JSON;
import com.mathbeta.microedge.entity.AgentConfig;
import com.mathbeta.microedge.entity.BaseMsg;
import com.mathbeta.microedge.entity.IdMsg;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.function.BiConsumer;

/**
 * websocket客户端构建者
 *
 * @author xuxiuyou
 * @date 2023/9/8 15:31
 */
@Slf4j
public class WebSocketClientBuilder {
    public static WebSocketClient build(String uri,
                                        BiConsumer<WebSocketClient, String> messageConsumer,
                                        AgentConfig config) {
        WebSocketClient webSocketClient = new WebSocketClient(URI.create(uri)) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                log.info("[websocket] 连接成功");
                this.sendId(this, config);
            }

            /**
             * 向master发送agent id等信息，表示agent已上线
             *
             * @param client
             * @param config
             */
            private void sendId(WebSocketClient client, AgentConfig config) {
                client.send(IdMsg.builder()
                        .type(BaseMsg.TYPE_ID)
                        .agentId(config.getAgent().getId())
                        .token(config.getAgent().getToken())
                        .build().toJsonString());
            }

            @Override
            public void onMessage(String message) {
                if (null != messageConsumer) {
                    messageConsumer.accept(this, message);
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                log.info("[websocket] 退出连接");
            }

            @Override
            public void onError(Exception ex) {
                log.info("[websocket] 连接错误={}", ex.getMessage());
            }
        };
        return webSocketClient;
    }
}
