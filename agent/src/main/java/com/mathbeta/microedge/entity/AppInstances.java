package com.mathbeta.microedge.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 被托管的应用实例
 *
 * @author xuxiuyou
 * @date 2023/9/12 14:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppInstances {
    private List<AppInstance> instances;
}
