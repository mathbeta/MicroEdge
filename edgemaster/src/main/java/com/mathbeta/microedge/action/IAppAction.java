package com.mathbeta.microedge.action;

/**
 * 应用操作接口
 *
 * @author xuxiuyou
 * @date 2023/8/12 12:24
 */
public interface IAppAction<U1, U2, U3, R> {

    /**
     * 消费三个参数并返回一个值
     *
     * @param u1
     * @param u2
     * @param u3
     * @return
     */
    R accept(U1 u1, U2 u2, U3 u3);

    default void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}