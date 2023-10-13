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
 * 节点信息
 *
 * @author xuxiuyou
 * @date 2023/09/07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "Node", description = "节点信息")
@XmlAccessorType(XmlAccessType.FIELD)
@KeyConfig(type = 1)
@TableName("edge_node")
public class Node extends BaseEntityWithAssignedId {


    @ApiModelProperty(name = "nsId", value = "命名空间id")
    @TableField("ns_id")
    private String nsId;


    @ApiModelProperty(name = "name", value = "节点名称")
    @TableField("name")
    private String name;


    @ApiModelProperty(name = "ip", value = "节点ip")
    @TableField("ip")
    private String ip;


    @ApiModelProperty(name = "token", value = "节点token")
    @TableField("token")
    private String token;


}
