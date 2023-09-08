package com.mathbeta.microedge.config;

import com.mathbeta.microedge.service.impl.WebSocketServerEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.io.IOException;

/**
 * websocket服务端配置
 *
 * @author xuxiuyou
 * @date 2023/9/8 11:31
 */
@Configuration
public class WebSocketConfig implements WebSocketConfigurer {
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        ServerEndpointExporter exporter = new ServerEndpointExporter();
        exporter.setAnnotatedEndpointClasses(WebSocketServerEndpoint.class);

        return exporter;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(new TextWebSocketHandler() {
//            @Override
//            public void handleTextMessage(WebSocketSession session, TextMessage message) {
//                System.out.println(String.format("Receive msg %s from client %s", message, session.getId()));
//                try {
//                    session.sendMessage(new TextMessage("Hello"));
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }, "/ws");
    }
}
