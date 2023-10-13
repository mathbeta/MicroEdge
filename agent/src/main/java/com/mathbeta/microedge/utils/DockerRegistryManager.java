package com.mathbeta.microedge.utils;

import com.google.common.collect.Maps;
import com.mathbeta.microedge.entity.Registry;

import java.util.Map;

/**
 * description
 *
 * @author xuxiuyou
 * @date 2023/10/8 17:18
 */
public class DockerRegistryManager {
    private static DockerRegistryManager manager = new DockerRegistryManager();

    private Map<String, Registry> registryMap = Maps.newConcurrentMap();

    private DockerRegistryManager() {
    }

    public static DockerRegistryManager getManager() {
        return manager;
    }

    public void addRegistry(String registryId, Registry registry) {
        this.registryMap.put(registryId, registry);
    }

    public void removeRegistry(String registryId) {
        this.registryMap.remove(registryId);
    }

    public Registry get(String registryId) {
        return registryMap.get(registryId);
    }
}
