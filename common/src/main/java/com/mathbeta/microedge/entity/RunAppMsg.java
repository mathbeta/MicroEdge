package com.mathbeta.microedge.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 运行App消息
 *
 * @author xuxiuyou
 * @date 2023/9/11 17:43
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class RunAppMsg extends BaseMsg {
    private String appName;
    private String containerName;
    private String image;
    private String registryId;
    private List<String> env;
    private List<String> portMappings;
    private List<String> volumeMappings;
}
