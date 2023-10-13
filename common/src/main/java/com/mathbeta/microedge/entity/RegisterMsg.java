package com.mathbeta.microedge.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * agent register msg
 *
 * @author xuxiuyou
 * @date 2023/9/9 下午4:09
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterMsg extends BaseMsg {
    private String ip;
    private String namespace;
}
