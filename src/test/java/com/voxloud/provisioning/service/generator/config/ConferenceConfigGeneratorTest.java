package com.voxloud.provisioning.service.generator.config;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConferenceConfigGeneratorTest {

    private ConferenceConfigGenerator generator = new ConferenceConfigGenerator();

    @Test
    public void testGenerate() {
        Map<String, String> config = new LinkedHashMap<String, String>();
        config.put("username", "john");
        config.put("password", "doe");
        config.put("domain", "sip.voxloud.com");
        config.put("port", "5060");
        config.put("codecs", "[\"G711\",\"G729\"]");

        String json = generator.generate(config);

        assertTrue(json.contains("\"username\" : \"john\""));
        assertTrue(json.contains("\"codecs\" : [ \"G711\", \"G729\" ]"));
    }
}