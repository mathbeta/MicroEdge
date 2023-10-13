package com.mathbeta.microedge.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * docker镜像仓库信息应答消息
 *
 * @author xuxiuyou
 * @date 2023/10/8 17:03
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RegistryRespMsg extends BaseMsg {
    private List<Registry> registries;
}
