package com.mathbeta.microedge.entity;

import com.mathbeta.alphaboot.annotation.KeyConfig;
import com.mathbeta.alphaboot.entity.BaseEntity;
import com.mathbeta.alphaboot.entity.BaseEntityWithAssignedId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("edge_namespace")
public class Namespace extends  BaseEntityWithAssignedId  {

    
    @ApiModelProperty(name = "name", value = "名称")
    @TableField("name")
    private String name;

    
    @ApiModelProperty(name = "description", value = "描述")
    @TableField("description")
    private String description;


}
