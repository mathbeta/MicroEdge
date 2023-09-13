package com.mathbeta.microedge.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * base message
 *
 * @author xuxiuyou
 * @date 2023/9/10 下午10:13
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BaseMsg {
    public static final String TYPE_ID = "id";
    public static final String TYPE_HEARTBEAT = "heartbeat";
    public static final String TYPE_RUN_APP = "run";
    public static final String TYPE_REMOVE_APP = "remove";
    public static final String TYPE_RESTART_APP = "restart";
    public static final String TYPE_UPGRADE_APP = "upgrade";


    private String type;
}
