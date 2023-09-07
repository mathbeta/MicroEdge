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
* 应用信息
*
* Created by xiuyou.xu on 2023/09/07.
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "App", description = "应用信息")
@XmlAccessorType(XmlAccessType.FIELD)
@KeyConfig(type = 1)
public class App extends BaseEntity {

    
    @ApiModelProperty(name = "name", value = "应用名称")
    private String name;

    
    @ApiModelProperty(name = "description", value = "描述")
    private String description;

    
    @ApiModelProperty(name = "nsId", value = "命名空间id")
    private String nsId;


}
