package com.mathbeta.microedge.utils;

import com.mathbeta.microedge.entity.AppInstance;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

/**
 * description
 *
 * @author xuxiuyou
 * @date 2023/9/13 17:47
 */
public class OptionalTest {
    @Test
    public void test() {
        Optional<AppInstance> optional = Optional.of(AppInstance.builder()
                .status("running")
                .containerId("container1")
                .build());
        Assertions.assertEquals("running", optional.map(AppInstance::getStatus).orElse("unknown"));
        Assertions.assertEquals("container1", optional.map(AppInstance::getContainerId).orElse(null));
    }
}
