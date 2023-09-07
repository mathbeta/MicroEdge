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
* 用户
*
* Created by xiuyou.xu on 2023/09/07.
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "User", description = "用户")
@XmlAccessorType(XmlAccessType.FIELD)
@KeyConfig(type = 1)
public class User extends BaseEntity {

    
    @ApiModelProperty(name = "name", value = "用户名")
    private String name;

    
    @ApiModelProperty(name = "password", value = "密码")
    private String password;

    
    @ApiModelProperty(name = "nsId", value = "命名空间id")
    private String nsId;


}
