package com.mathbeta.microedge.util;

import com.mathbeta.microedge.utils.DockerUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

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
}
