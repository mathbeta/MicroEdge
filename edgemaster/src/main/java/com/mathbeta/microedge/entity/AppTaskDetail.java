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
* 运维任务明细信息
*
* Created by xiuyou.xu on 2023/09/07.
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "AppTaskDetail", description = "运维任务明细信息")
@XmlAccessorType(XmlAccessType.FIELD)
@KeyConfig(type = 1)
@TableName("edge_app_task_detail")
public class AppTaskDetail extends  BaseEntityWithAssignedId  {

    
    @ApiModelProperty(name = "nodeId", value = "节点id")
    @TableField("node_id")
    private String nodeId;

    
    @ApiModelProperty(name = "appId", value = "应用id")
    @TableField("app_id")
    private String appId;

    
    @ApiModelProperty(name = "versionId", value = "应用版本id")
    @TableField("version_id")
    private String versionId;

    
    @ApiModelProperty(name = "taskId", value = "运维任务id")
    @TableField("task_id")
    private String taskId;

    
    @ApiModelProperty(name = "accepted", value = "用户是否已接受，仅升级版本任务使用")
    @TableField("accepted")
    private Integer accepted;

    
    @ApiModelProperty(name = "status", value = "当前运维状态，0-待升级；1-升级中；2-升级结束；3-还原中；4-还原结束；5-删除中；6-删除结束；7-部署中；8-部署结束；")
    @TableField("status")
    private Integer status;

    
    @ApiModelProperty(name = "result", value = "运维结果，0-成功，1-失败")
    @TableField("result")
    private Integer result;

    
    @ApiModelProperty(name = "reason", value = "运维结果原因")
    @TableField("reason")
    private String reason;


}
