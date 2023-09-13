package com.mathbeta.microedge.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * node register
 *
 * @author xuxiuyou
 * @date 2023/9/9
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NodeRegister {
    private String ip;
    private String namespace;
}
