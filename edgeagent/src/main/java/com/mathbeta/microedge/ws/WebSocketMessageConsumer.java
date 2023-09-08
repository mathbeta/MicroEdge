package com.mathbeta.microedge.ws;

import org.java_websocket.client.WebSocketClient;

import java.util.function.BiConsumer;

/**
 * 与云端管控服务之间的websocket交互消息处理
 *
 * @author xuxiuyou
 * @date 2023/9/8 17:41
 */
public class WebSocketMessageConsumer implements BiConsumer<WebSocketClient, String> {
    @Override
    public void accept(WebSocketClient webSocketClient, String s) {

    }
}
