package com.mathbeta.microedge.enums;

/**
 * 节点上应用任务状态
 *
 * @author xuxiuyou
 * @date 2023/8/9 10:39
 */
public enum NodeAppTaskStatus {
    SUBMITTED(0, "待执行"),
    UPGRADING(1, "升级中"),
    UPGRADE_ENDED(2, "升级结束"),
    ROLLBACK(3, "还原中"),
    ROLLBACK_ENDED(4, "还原结束"),
    REMOVING(5, "删除中"),
    REMOVE_ENDED(6, "删除结束"),
    DEPLOYING(7, "部署中"),
    DEPLOY_ENDED(8, "部署结束"),
    RESTARTING(9, "重启中"),
    RESTART_END(10, "重启结束");
    private Integer status;
    private String description;

    NodeAppTaskStatus(Integer status, String description) {
        this.status = status;
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }
}
