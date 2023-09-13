package com.mathbeta.microedge.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * agent register msg
 *
 * @author xuxiuyou
 * @date 2023/9/9 下午4:09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterMsg {
    private String ip;
    private String namespace;
}
