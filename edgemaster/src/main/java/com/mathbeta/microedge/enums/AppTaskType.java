package com.mathbeta.microedge.enums;

/**
 * 应用任务类型枚举
 *
 * @author xuxiuyou
 * @date 2023/9/11 11:19
 */
public enum AppTaskType {
    RESTART(0, "重启应用"),
    REMOVE(1, "关闭应用"),
    UPGRADE(2, "升级应用"),
    RUN(3, "部署应用");

    private Integer type;
    private String description;

    AppTaskType(Integer type, String description) {
        this.type = type;
        this.description = description;
    }

    public Integer getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }
}
