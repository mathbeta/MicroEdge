package com.mathbeta.microedge.ws;

import com.mathbeta.microedge.entity.AgentConfig;
import com.mathbeta.microedge.utils.ConfigHelper;
import com.mathbeta.microedge.utils.WebSocketClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;

/**
 * websocket客户端管理器
 *
 * @author xuxiuyou
 * @date 2023/9/12 15:59
 */
@Slf4j
public class WebSocketClientManager {
    private static WebSocketClientManager manager = new WebSocketClientManager();
    private AgentConfig config;
    private WebSocketClient client;

    private WebSocketClientManager() {
    }

    public static WebSocketClientManager getManager() {
        return manager;
    }

    /**
     * 初始化配置
     *
     * @param config
     */
    public void init(AgentConfig config) {
        this.config = config;
    }

    /**
     * 重连master
     */
    public void reconnect() {
        if (null != client) {
            client.close();
            client = null;
        }
        synchronized (WebSocketClientManager.class) {
            client = WebSocketClientBuilder.build((String) ConfigHelper.getInstance()
                    .get("master.websocket.address"), new WebSocketMessageConsumer(), config);
            client.connect();
        }
    }

    /**
     * 获取client
     *
     * @return
     */
    public WebSocketClient getClient() {
        if (null == client) {
            synchronized (WebSocketClientManager.class) {
                if (null == client) {
                    client = WebSocketClientBuilder.build((String) ConfigHelper.getInstance()
                            .get("master.websocket.address"), new WebSocketMessageConsumer(), config);
                    try {
                        client.connectBlocking();
                    } catch (InterruptedException e) {
                        log.error("Failed to wait connection established", e);
                    }
                }
            }
        }

        return client;
    }
}
