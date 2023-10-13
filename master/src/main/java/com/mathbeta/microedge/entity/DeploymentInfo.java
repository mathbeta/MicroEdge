package com.mathbeta.microedge.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 部署结果信息
 *
 * @author xuxiuyou
 * @date 2023/8/10 15:04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeploymentInfo {
    /**
     * 部署结果：0-成功；1-失败
     */
    private boolean result;
    /**
     * 应用名称
     */
    private String appName;
    /**
     * 应用实例id
     */
    private String appInstanceId;
}
