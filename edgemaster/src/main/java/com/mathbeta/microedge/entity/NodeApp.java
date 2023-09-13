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
import java.util.Objects;

/**
 * 节点应用关联表
 * <p>
 *
 * @author xuxiuyou
 * @date 2023/09/07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "NodeApp", description = "节点应用关联表")
@XmlAccessorType(XmlAccessType.FIELD)
@KeyConfig(type = 1)
@TableName("edge_node_app")
public class NodeApp extends BaseEntityWithAssignedId {


    @ApiModelProperty(name = "appId", value = "应用id")
    @TableField("app_id")
    private String appId;


    @ApiModelProperty(name = "versionId", value = "应用版本id")
    @TableField("version_id")
    private String versionId;


    @ApiModelProperty(name = "nodeId", value = "节点id")
    @TableField("node_id")
    private String nodeId;


    @ApiModelProperty(name = "enabled", value = "是否可用，逻辑删除标志字段")
    @TableField("enabled")
    private Integer enabled;


    @Override
    public boolean equals(Object o) {
        if (null != o && NodeApp.class.isInstance(o)) {
            NodeApp link = (NodeApp) o;
            return Objects.equals(this.getId(), link.getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (null != this.getId()) {
            return this.getId().hashCode();
        }
        return new StringBuilder(nodeId).append(appId).append(versionId).toString().hashCode();
    }
}
