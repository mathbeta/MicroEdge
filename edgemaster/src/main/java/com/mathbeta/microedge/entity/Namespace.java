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
* 命名空间
*
* Created by xiuyou.xu on 2023/09/07.
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "Namespace", description = "命名空间")
@XmlAccessorType(XmlAccessType.FIELD)
@KeyConfig(type = 1)
public class Namespace extends BaseEntity {

    
    @ApiModelProperty(name = "name", value = "名称")
    private String name;

    
    @ApiModelProperty(name = "description", value = "描述")
    private String description;


}
