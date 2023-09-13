package com.mathbeta.microedge.utils;

import com.alibaba.fastjson.JSONArray;

import java.util.List;
import java.util.stream.Collectors;

/**
 * json操作工具类
 *
 * @author xuxiuyou
 * @date 2023/9/11 18:04
 */
public class JsonUtil {
    public static List<String> jsonArrayToList(JSONArray ja) {
        if (null != ja && !ja.isEmpty()) {
            return ja.stream().map(e -> (String) e).collect(Collectors.toList());
        }
        return null;
    }
}
