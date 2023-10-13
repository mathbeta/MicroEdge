package com.mathbeta.microedge.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * docker镜像仓库信息请求消息
 *
 * @author xuxiuyou
 * @date 2023/10/8 16:52
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RegistryReqMsg extends BaseMsg {
    private String agentId;
}
