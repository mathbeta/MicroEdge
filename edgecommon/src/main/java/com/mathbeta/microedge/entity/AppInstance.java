package com.mathbeta.microedge.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 应用实例
 *
 * @author xuxiuyou
 * @date 2023/9/12 10:10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppInstance {
    private String status;
    private String nodeId;
    private String appName;
    private String containerId;
    private String containerName;
    private String image;
    private String versionNum;
    private String registryId;
    private List<String> env;
    private List<String> portMappings;
    private List<String> volumeMappings;
}
