package com.voxloud.provisioning.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voxloud.provisioning.entity.Device;
import com.voxloud.provisioning.repository.DeviceRepository;
import com.voxloud.provisioning.service.ProvisioningService;
import com.voxloud.provisioning.service.generator.config.ConferenceConfigGenerator;
import com.voxloud.provisioning.service.generator.config.DeskConfigGenerator;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

@Service
public class ProvisioningServiceImpl implements ProvisioningService {

    private final DeviceRepository deviceRepository;
    private final DeskConfigGenerator deskConfigGenerator;
    private final ConferenceConfigGenerator conferenceConfigGenerator;

    // These are the application-wide default values
    private static final String DEFAULT_DOMAIN = "sip.voxloud.com";
    private static final String DEFAULT_PORT = "5060";
    private static final List<String> DEFAULT_CODECS = Arrays.asList("G711", "G729", "OPUS");

    public ProvisioningServiceImpl(DeviceRepository deviceRepository, DeskConfigGenerator deskConfigGenerator, ConferenceConfigGenerator conferenceConfigGenerator) {
        this.deviceRepository = deviceRepository;
        this.deskConfigGenerator = deskConfigGenerator;
        this.conferenceConfigGenerator = conferenceConfigGenerator;
    }

    @Override
    public String getProvisioningFile(String macAddress) {
        Device device = deviceRepository.findByMacAddress(macAddress)
                .orElseThrow(() -> new IllegalArgumentException("Device not found for MAC: " + macAddress));

        Map<String, String> config = new LinkedHashMap<>();
        config.put("username", device.getUsername());
        config.put("password", device.getPassword());
        config.put("domain", DEFAULT_DOMAIN);
        config.put("port", DEFAULT_PORT);
        config.put("codecs", String.join(",", DEFAULT_CODECS));

        if (hasOverride(device)) {
            Map<String, String> override = parseOverride(device.getModel(), device.getOverrideFragment());
            override.forEach(config::put); // overrides or appends
        }

        return generateConfig(device.getModel(), config);
    }

    private boolean hasOverride(Device device) {
        String override = device.getOverrideFragment();
        return override != null && !override.trim().isEmpty();
    }

    private Map<String, String> parseOverride(Device.DeviceModel model, String fragment) {
        Map<String, String> override = new HashMap<>();
        try {
            if (model == Device.DeviceModel.DESK) {
                Properties props = new Properties();
                props.load(new StringReader(fragment));
                for (String name : props.stringPropertyNames()) {
                    override.put(name, props.getProperty(name));
                }
            } else if (model == Device.DeviceModel.CONFERENCE) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode node = objectMapper.readTree(fragment);
                node.fieldNames().forEachRemaining(key -> {
                    JsonNode valueNode = node.get(key);
                    if (valueNode.isArray() || valueNode.isObject()) {
                        override.put(key, valueNode.toString());
                    } else {
                        override.put(key, valueNode.asText());
                    }
                });
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse override fragment", e);
        }
        return override;
    }

    private String generateConfig(Device.DeviceModel model, Map<String, String> config) {
        if (model == Device.DeviceModel.DESK) {
            return deskConfigGenerator.generate(config);
        } else if (model == Device.DeviceModel.CONFERENCE) {
            return conferenceConfigGenerator.generate(config);
        } else {
            throw new IllegalArgumentException("Unsupported device model: " + model);
        }
    }
}