package com.mathbeta.microedge.entity;

import com.mathbeta.alphaboot.annotation.KeyConfig;
import com.mathbeta.alphaboot.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.Date;
/**
* 节点应用关联表
*
* Created by xiuyou.xu on 2023/09/07.
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "NodeApp", description = "节点应用关联表")
@XmlAccessorType(XmlAccessType.FIELD)
@KeyConfig(type = 1)
public class NodeApp extends BaseEntity {

    
    @ApiModelProperty(name = "appId", value = "应用id")
    private String appId;

    
    @ApiModelProperty(name = "versionId", value = "应用版本id")
    private String versionId;

    
    @ApiModelProperty(name = "nodeId", value = "节点id")
    private String nodeId;


}
