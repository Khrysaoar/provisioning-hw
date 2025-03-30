package com.voxloud.provisioning.service.generator.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voxloud.provisioning.service.generator.ConfigGenerator;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ConferenceConfigGenerator implements ConfigGenerator {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String generate(Map<String, String> config) {
        Map<String, Object> jsonMap = new LinkedHashMap<>();
        config.forEach((key, value) -> {
            if (key.equals("codecs") && value.startsWith("["))
                try {
                    jsonMap.put(key, mapper.readTree(value));
                } catch (IOException e) {
                    jsonMap.put(key, value);
                }
            else if (isNumeric(value)) {
                jsonMap.put(key, Integer.parseInt(value));
            } else {
                jsonMap.put(key, value);
            }
        });

        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonMap);
        } catch (IOException e) {
            throw new RuntimeException("JSON generation failed", e);
        }
    }

    private boolean isNumeric(String s) {
        try { Integer.parseInt(s); return true; }
        catch (NumberFormatException e) { return false; }
    }
}
