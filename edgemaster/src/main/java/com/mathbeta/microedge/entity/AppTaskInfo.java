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
* 主机应用运维任务信息
*
* Created by xiuyou.xu on 2023/09/07.
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "AppTaskInfo", description = "主机应用运维任务信息")
@XmlAccessorType(XmlAccessType.FIELD)
@KeyConfig(type = 1)
public class AppTaskInfo extends BaseEntity {

    
    @ApiModelProperty(name = "summary", value = "任务概述")
    private String summary;

    
    @ApiModelProperty(name = "type", value = "任务类型：0-重启；1-关闭；2-升级")
    private Integer type;

    
    @ApiModelProperty(name = "status", value = "任务状态，0-待执行；1-执行中；2-执行结束")
    private Integer status;

    
    @ApiModelProperty(name = "result", value = "升级结果，0-成功，1-失败")
    private Integer result;


}
