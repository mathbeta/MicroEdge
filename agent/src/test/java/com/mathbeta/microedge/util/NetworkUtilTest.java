package com.mathbeta.microedge.util;

import com.mathbeta.microedge.utils.NetworkUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * network util test
 *
 * @author xuxiuyou
 * @date 2023/9/9 下午3:47
 */
public class NetworkUtilTest {
    @Test
    public void test1() {
        Assertions.assertNotNull(NetworkUtil.getHostAddress());
    }

    @Test
    public void test2() {
        try {
            Assertions.assertNotNull(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
