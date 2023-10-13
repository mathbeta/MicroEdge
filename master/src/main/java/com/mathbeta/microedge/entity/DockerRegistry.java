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
 * docker镜像仓库
 *
 * @author xuxiuyou
 * @date 2023/09/18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "DockerRegistry", description = "docker镜像仓库")
@XmlAccessorType(XmlAccessType.FIELD)
@KeyConfig(type = 1)
@TableName("edge_docker_registry")
public class DockerRegistry extends  BaseEntityWithAssignedId  {

    
    @ApiModelProperty(name = "url", value = "docker镜像仓库地址")
    @TableField("url")
    private String url;

    
    @ApiModelProperty(name = "userEmail", value = "用户email")
    @TableField("user_email")
    private String userEmail;

    
    @ApiModelProperty(name = "userName", value = "用户名")
    @TableField("user_name")
    private String userName;

    
    @ApiModelProperty(name = "password", value = "密码")
    @TableField("password")
    private String password;

    
    @ApiModelProperty(name = "isPublic", value = "是否为公共仓库（公共仓库可以只通过url访问，不需要账号密码）")
    @TableField("is_public")
    private Integer isPublic;


}
