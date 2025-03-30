package com.voxloud.provisioning.service.generator;

import com.voxloud.provisioning.entity.Device;

import java.util.Map;

public interface ConfigGenerator {
    String generate(Map<String, String> config);
}