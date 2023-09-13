package com.mathbeta.microedge.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 升级应用消息
 *
 * @author xuxiuyou
 * @date 2023/9/12 10:08
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UpgradeAppMsg extends BaseMsg {
    private List<RunAppMsg> upgradeConfigs;
}
