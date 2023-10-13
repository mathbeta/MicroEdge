package com.mathbeta.microedge.config;

/**
 * agent常量
 *
 * @author xuxiuyou
 * @date 2023/9/9 下午4:11
 */
public class Constants {
    /**
     * 默认配置文件路径
     */
    public static final String DEFAULT_CONFIG_FILE = String.format("%s/.microedge-agent/config.yaml",
            System.getProperty("user.home"));
    /**
     * 存储应用实例信息的文件
     */
    public static final String APP_INSTANCE_INFO_FILE = String.format("%s/.microedge-agent/apps.yaml",
            System.getProperty("user.home"));
    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String JSON_CONTENT_TYPE = "application/json";

    public static final String APP_STATUS_RUNNING = "running";
    public static final String APP_STATUS_STOPPED = "exited";
    public static final String APP_STATUS_STARTING = "starting";
    public static final String APP_STATUS_REMOVING = "removing";
}
