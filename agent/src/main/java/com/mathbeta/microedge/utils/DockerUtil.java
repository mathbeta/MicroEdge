package com.mathbeta.microedge.utils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.PullImageResultCallback;
import com.github.dockerjava.jaxrs.JerseyDockerHttpClient;
import com.google.common.collect.Lists;
import com.mathbeta.microedge.entity.Registry;
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
    private static DockerClient dockerClient = DockerClientBuilder.getInstance()
            .withDockerHttpClient(new JerseyDockerHttpClient.Builder()
                    .dockerHost(URI.create(SystemUtils.IS_OS_LINUX
                            ? "unix:///var/run/docker.sock"
                            : "tcp://127.0.0.1:2375"))
                    .build()).build();

    /**
     * 列出容器
     *
     * @param all 是否列出所有容器，false表示只列出running状态的容器
     * @return
     */
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
        try {
            dockerClient.startContainerCmd(containerId).exec();
        } catch (NotFoundException e) {
            log.info("No such container {}", containerId);
        }
    }

    /**
     * 停止容器
     *
     * @param containerId
     */
    public static void stopContainer(String containerId) {
        try {
            dockerClient.stopContainerCmd(containerId).exec();
        } catch (NotFoundException e) {
            log.info("No such container {}", containerId);
        }
    }

    /**
     * 重启容器
     *
     * @param containerId
     */
    public static void restartContainer(String containerId) {
        try {
            dockerClient.restartContainerCmd(containerId).exec();
        } catch (NotFoundException e) {
            log.info("No such container {}", containerId);
        }
    }

    /**
     * 删除容器
     *
     * @param containerId
     */
    public static void removeContainer(String containerId) {
        try {
            dockerClient.removeContainerCmd(containerId).withForce(true).withRemoveVolumes(true).exec();
        } catch (NotFoundException e) {
            log.info("No such container {}", containerId);
        }
    }

    /**
     * 运行一个容器
     *
     * @param image
     * @param registryId
     * @param containerName
     * @param envVars，环境变量，格式：a=1
     * @param portMappings，端口映射，格式：8080:80/udp或8080:80
     * @param volumeMappings，卷映射，格式：/path/on/host:/path/in/container:ro或/path/on/host:/path/in/container
     * @return
     */
    public static String run(String image,
                             String registryId,
                             String containerName,
                             List<String> envVars,
                             List<String> portMappings,
                             List<String> volumeMappings) {
        Registry registry = DockerRegistryManager.getManager().get(registryId);
        if (null != registry) {
            image = String.format("%s/%s", registry.getUrl(), image);
        }
        CreateContainerCmd cmd = dockerClient.createContainerCmd(image)
                .withName(containerName);
        if (CollectionUtils.isNotEmpty(envVars)) {
            cmd.withEnv(envVars);
        }

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

        HostConfig hostConfig = HostConfig.newHostConfig().withPortBindings(ports);
        cmd.withHostConfig(hostConfig);
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
            hostConfig.withMounts(volumeMounts);
        }

        CreateContainerResponse resp = cmd.exec();
        String id = resp.getId();
        dockerClient.startContainerCmd(id).exec();
        Optional<String> statusOptional = status(id);
        String status = statusOptional.orElse("unknown");
        log.info("Running image {} container {} status {}", image, id, status);

        return id;
    }

    /**
     * 获取容器状态
     *
     * @param containerId
     * @return
     */
    public static Optional<String> status(String containerId) {
        Optional<String> result = Optional.empty();
        try {
            InspectContainerResponse inspectInfo = dockerClient.inspectContainerCmd(containerId).exec();
            InspectContainerResponse.ContainerState state = inspectInfo.getState();
            result = Optional.ofNullable(state.getStatus());
        } catch (NotFoundException e) {
            log.info("No such container {}", containerId);
        } catch (Exception e) {
            log.error("Error getting container status", e);
        }
        return result;
    }

    public static boolean pullImage(String imageName, String registryId) {
        Registry registry = DockerRegistryManager.getManager().get(registryId);
        log.info("Pulling image {} from registry [{}] with registry id {}", imageName,
                (null == registry ? "" : registry.getUrl()), registryId);
        if (null != registry) {
            return pullImage(String.format("%s/%s", registry.getUrl(), imageName), registry);
        }
        return false;
    }

    public static boolean pullImage(String imageName, Registry registry) {
        if (imageName.endsWith(":")) {
            log.error("Illegal image name, can not end with colon(:) {}", imageName);
            return false;
        }
        String tag = null, image;
        int tagIndex = imageName.lastIndexOf(":");
        if (tagIndex > 0) {
            String right = imageName.substring(tagIndex + 1);
            if (right.contains("/")) {
                image = imageName;
                tag = "latest";
            } else {
                image = imageName.substring(0, tagIndex);
                tag = right;
            }
        } else {
            image = imageName;
            tag = "latest";
        }

        try {
            PullImageCmd req =
                    registry.isPublic() ?
                            dockerClient.pullImageCmd(image).withRegistry(registry.getUrl()) :
                            dockerClient.pullImageCmd(image).withAuthConfig(new AuthConfig()
                                    .withRegistryAddress(registry.getUrl())
                                    .withEmail(registry.getUserEmail())
                                    .withUsername(registry.getUserName())
                                    .withPassword(registry.getPassword())
                            );
            req.withTag(tag);
            PullImageResultCallback res = new PullImageResultCallback();
            res = req.exec(res);
            res.awaitCompletion();

            return true;
        } catch (NotFoundException e) {
            log.error("Image not found {} from registry {}", imageName, registry.getUrl(), e);
            return false;
        } catch (NotModifiedException e) {
            log.warn("Image not modified {} from registry {}", imageName, registry.getUrl(), e);
            return true;
        } catch (Exception e) {
            log.error("Failed to pull image {} from registry {}", imageName, registry.getUrl(), e);
        }

        return false;
    }

    public static boolean existLocalImage(String image, String registryId) {
        try {
            Registry registry = DockerRegistryManager.getManager().get(registryId);
            if (null != registry) {
                image = String.format("%s/%s", registry.getUrl(), image);
            }
            InspectImageCmd cmd = dockerClient.inspectImageCmd(image);
            cmd.exec();
            return true;
        } catch (NotFoundException e) {
//            log.error("No local image {} found", image, e);
            return false;
        }
    }
}
