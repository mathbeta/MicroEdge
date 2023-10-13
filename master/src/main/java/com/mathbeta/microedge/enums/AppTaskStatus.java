package com.mathbeta.microedge.enums;

/**
 * 应用任务状态
 *
 * @author xuxiuyou
 * @date 2023/8/9 10:39
 */
public enum AppTaskStatus {
    SUBMITTED(0, "待执行"),
    EXECUTING(1, "执行中"),
    ENDED(2, "执行结束");
    private Integer status;
    private String description;

    AppTaskStatus(Integer status, String description) {
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
