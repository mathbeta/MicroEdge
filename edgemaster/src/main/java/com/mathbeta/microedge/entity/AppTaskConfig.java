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
 * 应用操作任务配置信息
 *
 * @author xuxiuyou
 * @date 2023/09/07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "AppTaskConfig", description = "应用操作任务配置信息")
@XmlAccessorType(XmlAccessType.FIELD)
@KeyConfig(type = 1)
@TableName("edge_app_task_config")
public class AppTaskConfig extends BaseEntityWithAssignedId {


    @ApiModelProperty(name = "taskId", value = "应用任务id")
    @TableField("task_id")
    private String taskId;


    @ApiModelProperty(name = "appId", value = "应用id")
    @TableField("app_id")
    private String appId;


    @ApiModelProperty(name = "appVersionId", value = "目标应用版本id")
    @TableField("app_version_id")
    private String appVersionId;


    @ApiModelProperty(name = "order", value = "多应用任务执行顺序")
    @TableField("order")
    private Integer order;


}
