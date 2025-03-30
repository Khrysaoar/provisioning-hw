package com.voxloud.provisioning.service.generator.config;

import com.voxloud.provisioning.service.generator.ConfigGenerator;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DeskConfigGenerator implements ConfigGenerator {

    @Override
    public String generate(Map<String, String> config) {
        return config.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("\n"));
    }
}