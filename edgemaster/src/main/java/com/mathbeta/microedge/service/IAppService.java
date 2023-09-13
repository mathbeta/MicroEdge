package com.mathbeta.microedge.service;


import com.alibaba.fastjson.JSONArray;
import com.mathbeta.alphaboot.entity.Result;
import com.mathbeta.alphaboot.service.IService;
import com.mathbeta.microedge.entity.App;
import com.mathbeta.microedge.entity.AppTaskConfig;
import com.mathbeta.microedge.entity.AppVersion;
import com.mathbeta.microedge.entity.DeploymentInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 应用信息
 *
 * @author xuxiuyou
 * @date 2023/09/07
 */
@Service
public interface IAppService extends IService<App> {
    /**
     * 部署应用实例
     *
     * @param appId
     * @param nodeId
     * @param appVersion
     * @param env
     * @param portMappings
     * @param volumeMappings
     * @return
     */
    DeploymentInfo runApp(String appId, String nodeId, AppVersion appVersion, JSONArray env, JSONArray portMappings,
                          JSONArray volumeMappings);

    /**
     * 删除应用实例
     *
     * @param nodeId
     * @param nodeAppId
     * @return
     */
    boolean removeApp(String nodeId, String nodeAppId);

    /**
     * 重启应用实例
     *
     * @param nodeId
     * @param nodeAppId
     * @return
     */
    boolean restartApp(String nodeId, String nodeAppId);

    /**
     * 升级应用实例
     *
     * @param nodeId
     * @param upgradeTaskId
     * @param appTaskConfigs
     * @return
     */
    boolean upgradeApp(String nodeId, String upgradeTaskId, List<AppTaskConfig> appTaskConfigs);

    /**
     * 查询节点应用列表
     *
     * @param nodeId
     * @return
     */
    Result listApps(String nodeId);
}
