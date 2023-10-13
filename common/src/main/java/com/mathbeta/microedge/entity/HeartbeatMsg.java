package com.mathbeta.microedge.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * heartbeat to cloud master
 *
 * @author xuxiuyou
 * @date 2023/9/9 下午3:04
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class HeartbeatMsg extends BaseMsg {
    private String agentId;
    private String token;
    private List<AppInstance> appInstances;
}
