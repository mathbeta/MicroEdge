package com.mathbeta.microedge.util;

import com.alibaba.fastjson.JSONObject;
import com.mathbeta.alphaboot.utils.TableUtil;
import com.mathbeta.microedge.entity.AgentConfig;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.util.Date;

/**
 * description
 *
 * @author xuxiuyou
 * @date 2023/9/9 下午6:52
 */
public class AgentConfigTest {
    @Test
    public void testYaml() {
        Yaml yaml = new Yaml();
        AgentConfig config = AgentConfig.builder()
                .agent(AgentConfig.InnerConfig.builder()
                        .id(TableUtil.uuid())
                        .token(TableUtil.uuid())
                        .ip("192.168.1.2")
                        .registerTime(new Date())
                        .build())
                .build();
        System.out.println(yaml.dumpAsMap(config));
    }

    @Test
    public void testMap() {
        Yaml yaml = new Yaml();
        JSONObject jo = new JSONObject();
        JSONObject agent = new JSONObject();
        agent.put("id", TableUtil.uuid());
        agent.put("token", TableUtil.uuid());
        agent.put("ip", "192.168.2.3");
        agent.put("registerTime", new Date());
        jo.put("agent", agent);
        System.out.println(yaml.dumpAsMap(jo));
    }
}
