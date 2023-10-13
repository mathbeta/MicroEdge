package com.mathbeta.microedge.enums;

/**
 * 应用任务结果
 *
 * @author xuxiuyou
 * @date 2023/8/9 10:39
 */
public enum AppTaskResult {
    SUCCESS(0, "成功"),
    FAIL(1, "失败");
    private Integer result;
    private String description;

    AppTaskResult(Integer result, String description) {
        this.result = result;
        this.description = description;
    }

    public Integer getResult() {
        return result;
    }

    public String getDescription() {
        return description;
    }
}
