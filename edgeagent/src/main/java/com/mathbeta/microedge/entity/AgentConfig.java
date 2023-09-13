package com.mathbeta.microedge.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * agent核心配置项
 *
 * @author xuxiuyou
 * @date 2023/9/9 下午3:29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentConfig {
    private InnerConfig agent;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InnerConfig {
        private String id;

        private String token;

        private String ip;

        private Date registerTime;
    }
}
