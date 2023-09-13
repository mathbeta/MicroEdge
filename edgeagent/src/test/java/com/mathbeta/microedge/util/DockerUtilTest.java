package com.mathbeta.microedge.util;

import com.google.common.collect.Lists;
import com.mathbeta.microedge.utils.DockerUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
                "nginx-test",
                Lists.newArrayList("a=1"),
                Lists.newArrayList("8088:80", "8089:80"),
                Lists.newArrayList("/d/etc:/tmp/test")));
    }
}
