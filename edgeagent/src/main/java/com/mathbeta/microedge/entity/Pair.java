package com.mathbeta.microedge.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * description
 *
 * @author xuxiuyou
 * @date 2023/9/7 18:11
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pair<L,R> {
    private L left;
    private R right;
}
