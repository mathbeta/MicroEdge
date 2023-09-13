package com.mathbeta.microedge.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mathbeta.alphaboot.annotation.KeyConfig;
import com.mathbeta.alphaboot.entity.BaseEntityWithAssignedId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * 用户
 *
 * @author xuxiuyou
 * @date 2023/09/07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "User", description = "用户")
@XmlAccessorType(XmlAccessType.FIELD)
@KeyConfig(type = 1)
@TableName("edge_user")
public class User extends BaseEntityWithAssignedId {


    @ApiModelProperty(name = "name", value = "用户名")
    @TableField("name")
    private String name;


    @ApiModelProperty(name = "password", value = "密码")
    @TableField("password")
    private String password;


    @ApiModelProperty(name = "nsId", value = "命名空间id")
    @TableField("ns_id")
    private String nsId;


}
