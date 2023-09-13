package com.mathbeta.microedge.utils;

import lombok.extern.slf4j.Slf4j;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * network util
 *
 * @author xuxiuyou
 * @date 2023/9/9 下午3:39
 */
@Slf4j
public class NetworkUtil {
    public static String getHostAddress() {
        try {
            // 获取所有网络接口
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                // 获取每个网络接口的所有IP地址
                NetworkInterface ni = nis.nextElement();
                if (ni.isLoopback() || ni.isVirtual() || !ni.isUp()) {
                    continue;
                }
                if (ni.getName().startsWith("veth")
                        || ni.getName().startsWith("docker")
                        || ni.getName().startsWith("bridge")) {
                    continue;
                }

                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    // 排除LoopbackAddress、LinkLocalAddress、SiteLocalAddress
                    if (address instanceof Inet4Address
                            && !address.isLoopbackAddress()
                            && !address.isLinkLocalAddress()) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            log.error("Failed to get host address", e);
        }
        return null;
    }
}
