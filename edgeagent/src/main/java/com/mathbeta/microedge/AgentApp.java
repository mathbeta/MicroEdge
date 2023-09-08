package com.mathbeta.microedge;

import com.mathbeta.microedge.utils.ConfigHelper;
import com.mathbeta.microedge.utils.WebSocketClientBuilder;
import com.mathbeta.microedge.ws.WebSocketMessageConsumer;
import org.java_websocket.client.WebSocketClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * agent app
 *
 * @author xuxiuyou
 * @date 2023/9/7 17:35
 */
public class AgentApp {
    /**
     * 业务处理线程池
     */
    public static final ExecutorService workers = Executors.newFixedThreadPool(64);

    public static void main(String[] args) {
        startWebSocketClient();
    }

    /**
     * 启动连接云端管控服务websocket连接
     */
    private static void startWebSocketClient() {
        WebSocketClient client = WebSocketClientBuilder.build((String) ConfigHelper.getInstance()
                .get("agent.websocket.serverAddress"), new WebSocketMessageConsumer());
        client.connect();
    }
}
