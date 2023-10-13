package com.mathbeta.microedge.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 应用任务状态应答
 *
 * @author xuxiuyou
 * @date 2023/8/10 9:51
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "应用任务状态应答")
public class AppTaskStatusResp {
    @Schema(description = "节点id")
    private String nodeId;
    @Schema(description = "应用id")
    private String appId;
    @Schema(description = "应用版本id")
    private String versionId;
    @Schema(description = "节点应用运维状态")
    private Integer status;
    @Schema(description = "节点应用运维结果")
    private Integer result;
    @Schema(description = "节点应用运维结果原因")
    private String reason;
}
