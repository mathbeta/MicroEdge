package com.mathbeta.microedge.utils;

import com.alibaba.fastjson.JSON;
import com.mathbeta.microedge.entity.BaseMsg;
import com.mathbeta.microedge.entity.IdMsg;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * json测试
 *
 * @author xuxiuyou
 * @date 2023/9/11 14:16
 */
public class JsonTest {
    @Test
    public void testDeserialization() {
        String s = "{\"type\":\"id\",\"agentId\":\"123\",\"token\":\"my-token\"}";
        BaseMsg baseMsg = JSON.parseObject(s, BaseMsg.class);
        Assertions.assertEquals("id", baseMsg.getType());

        Assertions.assertFalse(baseMsg instanceof IdMsg);
    }
}
