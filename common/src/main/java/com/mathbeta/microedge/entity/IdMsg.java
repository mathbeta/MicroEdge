package com.mathbeta.microedge.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * id消息
 *
 * @author xuxiuyou
 * @date 2023/9/11
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class IdMsg extends BaseMsg {
    private String agentId;
    private String token;
}
