package com.mathbeta.microedge.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

/**
 * yaml serialization and deserialization
 *
 * @author xuxiuyou
 * @date 2023/9/14 17:17
 */
@Slf4j
public class YamlUtil {
    private static ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    public static <T> T loadAs(InputStream is, Class<T> cls) {
        try {
            return mapper.readValue(is, cls);
        } catch (IOException e) {
            log.error("Failed to read value from input stream to class {}", cls, e);
        }
        return null;
    }

    public static String dumpAsMap(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.error("Failed to write value as string {}", o, e);
        }
        return null;
    }
}
