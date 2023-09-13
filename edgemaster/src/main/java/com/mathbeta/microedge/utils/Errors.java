package com.mathbeta.microedge.utils;

import lombok.Builder;
import lombok.Data;

/**
 * error info
 *
 * @author xuxiuyou
 * @date 2023/9/9 下午5:16
 */
@Data
@Builder
public class Errors {
    /**
     * 命名空间不存在
     */
    public static final Errors NAMESPACE_NOT_EXISTS = Errors.builder()
            .code("30001")
            .message("Namespace not exists")
            .build();
    /**
     * 向线程池提交应用任务失败
     */
    public static final Errors FAILED_TO_SUBMIT_APP_TASK = Errors.builder()
            .code("30002")
            .message("Failed to submit app task")
            .build();

    private String code;
    private String message;
}
