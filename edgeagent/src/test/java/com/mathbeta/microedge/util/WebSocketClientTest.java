package com.mathbeta.microedge.util;

import com.mathbeta.microedge.utils.WebSocketClientBuilder;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * websocket客户端测试
 *
 * @author xuxiuyou
 * @date 2023/9/8 15:38
 */
public class WebSocketClientTest {
    @Test
    public void test() throws InterruptedException {
        WebSocketClient client = WebSocketClientBuilder.build("ws://127.0.0.1:8090/master/ws", (webSocketClient, msg) -> {
            Assertions.assertEquals(ReadyState.OPEN, webSocketClient.getReadyState());
            webSocketClient.send("hello");
        });
        client.connectBlocking();

        Thread.sleep(1000L);
        client.close();
    }
}
