package com.mathbeta.microedge.config;

import com.mathbeta.microedge.service.impl.WebSocketServerEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * websocket服务端配置
 *
 * @author xuxiuyou
 * @date 2023/9/8 11:31
 */
@Configuration
public class WebSocketConfig {
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        ServerEndpointExporter exporter = new ServerEndpointExporter();
        exporter.setAnnotatedEndpointClasses(WebSocketServerEndpoint.class);

        return exporter;
    }
}
