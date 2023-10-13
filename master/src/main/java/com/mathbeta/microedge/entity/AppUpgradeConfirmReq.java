package com.mathbeta.microedge.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 开始升级应用请求参数
 *
 * @author xuxiuyou
 * @date 2023/8/10 16:55
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "开始升级应用请求参数")
public class AppUpgradeConfirmReq {
    @Schema(description = "节点id列表")
    private List<String> nodeIds;
    @Schema(description = "升级任务id")
    private String upgradeTaskId;
    @Schema(description = "是否接受升级")
    private Boolean accepted;
}
