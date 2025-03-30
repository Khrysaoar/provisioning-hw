package com.voxloud.provisioning.service.generator.config;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeskConfigGeneratorTest {

    private DeskConfigGenerator generator = new DeskConfigGenerator();

    @Test
    public void testGenerate() {
        Map<String, String> config = new LinkedHashMap<String, String>();
        config.put("username", "john");
        config.put("password", "doe");
        config.put("domain", "sip.voxloud.com");
        config.put("port", "5060");

        String result = generator.generate(config);

        assertTrue(result.contains("username=john"));
        assertTrue(result.contains("domain=sip.voxloud.com"));
    }
}