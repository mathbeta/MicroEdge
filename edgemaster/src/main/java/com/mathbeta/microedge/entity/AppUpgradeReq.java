package com.mathbeta.microedge.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 应用升级请求参数
 *
 * @author xuxiuyou
 * @date 2023/8/8 15:10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NotNull
public class AppUpgradeReq {
    @NotEmpty
    private List<String> nodeIds;
    @NotEmpty
    private List<String> versionIds;
}
