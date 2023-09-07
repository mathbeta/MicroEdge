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
* 节点信息
*
* Created by xiuyou.xu on 2023/09/07.
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "Node", description = "节点信息")
@XmlAccessorType(XmlAccessType.FIELD)
@KeyConfig(type = 1)
public class Node extends BaseEntity {

    
    @ApiModelProperty(name = "nsId", value = "命名空间id")
    private String nsId;

    
    @ApiModelProperty(name = "name", value = "节点名称")
    private String name;

    
    @ApiModelProperty(name = "ip", value = "节点ip")
    private String ip;

    
    @ApiModelProperty(name = "token", value = "节点token")
    private String token;


}
