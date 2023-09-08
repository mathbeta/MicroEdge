package com.mathbeta.microedge.utils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.jaxrs.JerseyDockerHttpClient;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.SystemUtils;

import java.net.URI;
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
    private static DockerClient dockerClient =
            DockerClientBuilder.getInstance()
                    .withDockerHttpClient(new JerseyDockerHttpClient.Builder()
                            .dockerHost(URI.create(SystemUtils.IS_OS_WINDOWS ? "tcp://127.0.0.1:2375" :
                                    (SystemUtils.IS_OS_LINUX ? "/var/run/docker.sock" : "")))
                            .build())
                    .build();

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
     * 重启容器
     *
     * @param containerId
     */
    public static void restartContainer(String containerId) {
        dockerClient.restartContainerCmd(containerId).exec();
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
     * @param envVars，环境变量，格式：a=1
     * @param portMappings，端口映射，格式：8080:80/udp或8080:80
     * @param volumeMappings，卷映射，格式：/path/on/host:/path/in/container:ro或/path/on/host:/path/in/container
     * @return
     */
    public static String run(String image,
                             String containerName,
                             List<String> envVars,
                             List<String> portMappings,
                             List<String> volumeMappings) {
        CreateContainerCmd cmd = dockerClient.createContainerCmd(image)
                .withName(containerName).withEnv(envVars);

        Ports ports = new Ports();
        List<ExposedPort> exposedPorts = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(portMappings)) {
            portMappings.forEach(mapping -> {
                boolean udp = false;
                int inside;
                int outside;
                String pm = mapping;
                if (mapping.contains("/")) {
                    String[] m = mapping.split("/");
                    udp = "udp".equals(m[1].toLowerCase());

                    pm = m[0];
                }
                String[] p = pm.split(":");
                outside = Integer.parseInt(p[0]);
                inside = Integer.parseInt(p[1]);
                ExposedPort internal = udp ?
                        ExposedPort.udp(inside) :
                        ExposedPort.tcp(inside);
                Ports.Binding external = Ports.Binding.bindPort(outside);
                ports.bind(internal, external);
                exposedPorts.add(internal);
            });
            cmd.withExposedPorts(exposedPorts);
        }

        HostConfig hostConfig = HostConfig.newHostConfig();
        List<Mount> volumeMounts = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(volumeMappings)) {
            volumeMappings.forEach(mapping -> {
                String[] m = mapping.split(":");
                boolean typeBind = true;
                String hostDest = null;
                String containerDest = null;
                boolean readOnly = false;
                if (null != m) {
                    if (m.length == 1) {
                        typeBind = false;
                        containerDest = m[0];
                    } else {
                        hostDest = m[0];
                        containerDest = m[1];
                    }
                    if (m.length == 3) {
                        readOnly = "ro".equals(m[2].toLowerCase());
                    }
                }

                Mount mount = (new Mount())
                        .withSource(hostDest)
                        .withType(typeBind ? MountType.BIND : MountType.VOLUME)
                        .withTarget(containerDest)
                        .withReadOnly(readOnly);
                volumeMounts.add(mount);
            });
            hostConfig.withPortBindings(ports).withMounts(volumeMounts);
            cmd.withHostConfig(hostConfig);
        }

        CreateContainerResponse resp = cmd.exec();
        String id = resp.getId();
        dockerClient.startContainerCmd(id).exec();
        Optional<String> statusOptional = getContainerStatus(id);
        String status = statusOptional.orElse("unknown");
        log.info("Running image {} container {} status {}", image, id, status);

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
