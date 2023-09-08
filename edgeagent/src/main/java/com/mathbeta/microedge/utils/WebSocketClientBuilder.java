package com.mathbeta.microedge.utils;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * websocket客户端构建者
 *
 * @author xuxiuyou
 * @date 2023/9/8 15:31
 */
@Slf4j
public class WebSocketClientBuilder {
    public static WebSocketClient build(String uri, BiConsumer<WebSocketClient, String> messageConsumer) {
            WebSocketClient webSocketClient = new WebSocketClient(URI.create(uri)) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    log.info("[websocket] 连接成功");
                }

                @Override
                public void onMessage(String message) {
                    log.info("[websocket] 收到消息={}", message);
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
