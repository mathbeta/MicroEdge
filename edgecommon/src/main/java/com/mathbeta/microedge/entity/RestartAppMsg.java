package com.mathbeta.microedge.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 重启app消息
 *
 * @author xuxiuyou
 * @date 2023/9/12 9:54
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class RestartAppMsg extends BaseMsg {
    private String containerName;
}
