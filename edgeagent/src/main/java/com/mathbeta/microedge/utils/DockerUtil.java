package com.mathbeta.microedge.utils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * docker容器操作工具类
 *
 * @author xuxiuyou
 * @date 2023/9/7 17:55
 */
@Slf4j
public class DockerUtil {
    private static DockerClient dockerClient = DockerClientBuilder.getInstance("tcp://127.0.0.1:2375").build();

    public static List<String> ps(boolean all) {
        List<Container> containers = dockerClient.listContainersCmd().withShowAll(all).exec();
        if (CollectionUtils.isNotEmpty(containers)) {
            return containers.stream().map(c -> c.getId()).collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    /**
     * 启动容器
     *
     * @param containerId
     */
    public static void startContainer(String containerId) {
        dockerClient.startContainerCmd(containerId).exec();
    }

    /**
     * 启动容器
     *
     * @param containerId
     */
    public static void stopContainer(String containerId) {
        dockerClient.stopContainerCmd(containerId).exec();
    }

    /**
     * 删除容器
     *
     * @param containerId
     */
    public static void removeContainer(String containerId) {
        dockerClient.removeContainerCmd(containerId).exec();
    }

    /**
     * 运行一个容器
     *
     * @param image
     * @param containerName
     * @param envVars
     * @param portMappings
     * @param volumeMappings
     * @return
     */
    public static String run(String image, String containerName,
                             List<String> envVars,
                             List<String> portMappings,
                             List<String> volumeMappings) {
        CreateContainerCmd cmd = dockerClient.createContainerCmd(image)
                .withName(containerName).withEnv(envVars);


        List<ExposedPort> exposedPorts = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(portMappings)) {
//            portMappings.forEach(mapping -> {
//                ExposedPort internal = mapping.isUdp() ?
//                        ExposedPort.udp(mapping.getInside()) :
//                        ExposedPort.tcp(mapping.getInside());
//                Ports.Binding external = Ports.Binding.bindPort(mapping.getOutside());
//                portBindings.bind(internal, external);
//                exposedPorts.add(internal);
//            });
            cmd.withExposedPorts(exposedPorts);
        }

        HostConfig hostConfig = HostConfig.newHostConfig();
        List<Mount> volumeMounts = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(volumeMappings)) {
//            volumeMappings.forEach(mapping -> {
//                Mount mount = (new Mount())
//                        .withSource(volumeMapping.getHostDestination())
//                        .withType(volumeMapping.getType() == VolumeMappingType.BIND ? MountType.BIND : MountType.VOLUME)
//                        .withTarget(volumeMapping.getContainerDestination())
//                        .withReadOnly(isReadOnly);
//                volumeMounts.add(mount);
//            });
            hostConfig.withMounts(volumeMounts);
            cmd.withHostConfig(hostConfig);
        }

        CreateContainerResponse resp = cmd.exec();
        String id = resp.getId();
        dockerClient.startContainerCmd(id);
        Optional<String> statusOptional = getContainerStatus(id);
        String status = statusOptional.orElse("unknown");
        log.debug("Running image {} container {} status {}", image, id, status);

        return id;
    }

    public static Optional<String> getContainerStatus(String containerId) {
        Optional<String> result = Optional.empty();
        try {
            log.debug("Start get Container status containerID: {}", containerId);
            InspectContainerResponse inspectInfo = dockerClient.inspectContainerCmd(containerId).exec();
            InspectContainerResponse.ContainerState state = inspectInfo.getState();
            result = Optional.ofNullable(state.getStatus());
        } catch (Exception exp) {
            log.error("Error getting container status", exp);
        }
        log.debug("Finished get Container status containerID: {}", containerId);
        return result;
    }
}
