package com.mathbeta.microedge.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 应用任务信息请求参数
 *
 * @author xuxiuyou
 * @date 2023/8/8 15:10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "应用任务信息请求参数")
public class AppTaskInfoReq {
    @Schema(description = "节点id列表")
    private List<String> nodeIds;
    @Schema(description = "应用名称")
    private String appName;
    @Schema(description = "应用版本id")
    private String versionId;
    @Schema(description = "重试次数")
    private int retries = 3;
}
