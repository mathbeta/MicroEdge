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
* 应用版本
*
* Created by xiuyou.xu on 2023/09/07.
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "AppVersion", description = "应用版本")
@XmlAccessorType(XmlAccessType.FIELD)
@KeyConfig(type = 1)
public class AppVersion extends BaseEntity {

    
    @ApiModelProperty(name = "appId", value = "应用id")
    private String appId;

    
    @ApiModelProperty(name = "versionNum", value = "版本号")
    private String versionNum;

    
    @ApiModelProperty(name = "image", value = "镜像名称")
    private String image;

    
    @ApiModelProperty(name = "description", value = "描述")
    private String description;


}
