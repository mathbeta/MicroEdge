package com.mathbeta.microedge;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.mathbeta.microedge.service.impl.WebSocketServerEndpoint;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

/**
 * edge master程序入口
 *
 * @author xuxiuyou
 * @date 2018/11/20 18:22
 */
@SpringBootApplication(scanBasePackages = "com.mathbeta.microedge")
@MapperScan("com.mathbeta.microedge.mapper")
@EnableWebSocket
@EnableKnife4j
public class MasterApp {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MasterApp.class, args);
        WebSocketServerEndpoint.setApplicationContext(context);
    }
}
