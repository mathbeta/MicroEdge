package com.mathbeta.microedge.util;

import com.google.common.collect.Lists;
import com.mathbeta.microedge.entity.Registry;
import com.mathbeta.microedge.utils.DockerUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * description
 *
 * @author xuxiuyou
 * @date 2023/9/7 17:56
 */
public class DockerUtilTest {
    @Test
    public void testPs() {
        assertFalse(DockerUtil.ps(true).isEmpty());
    }

    @Test
    public void testRestart() {
        DockerUtil.restartContainer("e83695bb36ce");
    }

    @Test
    public void testRun() {
        assertNotNull(DockerUtil.run("nginx:latest",
                "1",
                "nginx-test",
                Lists.newArrayList("a=1"),
                Lists.newArrayList("8088:80", "8089:80"),
                Lists.newArrayList("/d/etc:/tmp/test")));
    }

    @Test
    public void testPullImage() {
        assertTrue(DockerUtil.pullImage("192.168.5.9:60001/mytest/recovery:v1.0", Registry.builder()
                .isPublic(false)
                .url("192.168.5.9:60001")
                .userName("xuxiuyou")
                .userEmail("xuxiuyou@budee.cn")
                .password("Budee123")
                .build()));
    }
}
