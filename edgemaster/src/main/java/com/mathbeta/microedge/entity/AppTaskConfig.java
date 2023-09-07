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
* 应用操作任务配置信息
*
* Created by xiuyou.xu on 2023/09/07.
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "AppTaskConfig", description = "应用操作任务配置信息")
@XmlAccessorType(XmlAccessType.FIELD)
@KeyConfig(type = 1)
public class AppTaskConfig extends BaseEntity {

    
    @ApiModelProperty(name = "taskId", value = "运维任务id")
    private String taskId;

    
    @ApiModelProperty(name = "appId", value = "应用id")
    private String appId;

    
    @ApiModelProperty(name = "appVersionId", value = "目标应用版本id")
    private String appVersionId;

    
    @ApiModelProperty(name = "order", value = "应用运维任务执行顺序")
    private Integer order;


}
