package com.mathbeta.microedge.utils;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * 配置文件帮助类
 *
 * @author xuxiuyou
 * @date 2023/8/24 18:16
 */
@Slf4j
public class ConfigHelper {
    private Map<String, Object> config;
    private static ConfigHelper instance = new ConfigHelper();

    private ConfigHelper() {
        Yaml yaml = new Yaml();
        try (InputStream is = ConfigHelper.class.getResourceAsStream("/config.yaml")) {
            config = yaml.loadAs(is, Map.class);
        } catch (IOException e) {
            log.error("Failed to read config.xml", e);
        }
    }

    public static ConfigHelper getInstance() {
        return instance;
    }

    public Object get(String key) {
        if (null != key && key.contains(".")) {
            String[] ks = key.split("\\.");
            Map<String, Object> obj = (Map<String, Object>) config.get(ks[0]);
            for (int i = 1; i < ks.length; i++) {
                if (null != obj) {
                    if (i < ks.length - 1) {
                        obj = (Map<String, Object>) obj.get(ks[i]);
                    } else {
                        return obj.get(ks[i]);
                    }
                }
            }
            throw new RuntimeException(String.format("Unknown key in config %s", key));
        }
        return config.get(key);
    }
}
