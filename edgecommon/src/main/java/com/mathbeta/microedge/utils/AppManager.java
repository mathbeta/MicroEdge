package com.mathbeta.microedge.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mathbeta.microedge.entity.AppInstance;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 应用实例管理
 *
 * @author xuxiuyou
 * @date 2023/9/12 10:10
 */
public class AppManager {
    private static AppManager manager = new AppManager();
    /**
     * 容器名称 => 应用实例信息
     */
    private Map<String, AppInstance> appInstanceMap = Maps.newConcurrentMap();

    private AppManager() {
    }

    public static AppManager getManager() {
        return manager;
    }

    public void addInstance(String containerName, AppInstance instance) {
        appInstanceMap.put(containerName, instance);
    }

    public void removeInstance(String id) {
        appInstanceMap.remove(id);
    }

    public List<AppInstance> listInstances() {
        return ImmutableList.copyOf(appInstanceMap.values().stream().collect(Collectors.toList()));
    }

    /**
     * 判断是否存在指定的应用容器
     *
     * @param containerName
     * @return
     */
    public boolean checkExistence(String containerName) {
        return appInstanceMap.containsKey(containerName);
    }

    /**
     * 根据应用名称查询实例容器名称
     *
     * @param appName
     * @return
     */
    public String query(String appName) {
        return appInstanceMap.values().stream()
                .filter(e -> Objects.equals(appName, e.getAppName()))
                .map(e -> e.getContainerName()).findFirst()
                .get();
    }

    /**
     * 根据容器名称查询应用实例
     *
     * @param containerName
     * @return
     */
    public Optional<AppInstance> queryByContainerName(String containerName) {
        return Optional.ofNullable(appInstanceMap.get(containerName));
    }
}
