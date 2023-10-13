package com.mathbeta.microedge.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 删除app消息
 *
 * @author xuxiuyou
 * @date 2023/9/12 9:54
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class RemoveAppMsg extends BaseMsg {
    private String containerName;
}
