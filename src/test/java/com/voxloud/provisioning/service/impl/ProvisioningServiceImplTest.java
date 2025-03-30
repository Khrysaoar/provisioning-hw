package com.voxloud.provisioning.service.impl;

import com.voxloud.provisioning.entity.Device;
import com.voxloud.provisioning.repository.DeviceRepository;
import com.voxloud.provisioning.service.generator.config.ConferenceConfigGenerator;
import com.voxloud.provisioning.service.generator.config.DeskConfigGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProvisioningServiceImplTest {

    private DeviceRepository deviceRepository;
    private DeskConfigGenerator deskGenerator;
    private ConferenceConfigGenerator confGenerator;
    private ProvisioningServiceImpl provisioningService;

    @BeforeEach
    void setup() {
        deviceRepository = mock(DeviceRepository.class);
        deskGenerator = mock(DeskConfigGenerator.class);
        confGenerator = mock(ConferenceConfigGenerator.class);
        provisioningService = new ProvisioningServiceImpl(deviceRepository, deskGenerator, confGenerator);
    }

    @Test
    void testDeskDeviceWithoutOverride() {
        var device = createDevice(Device.DeviceModel.DESK, null);
        when(deviceRepository.findByMacAddress("aa-bb-cc")).thenReturn(Optional.of(device));
        when(deskGenerator.generate(anyMap())).thenReturn("""
            username=john
            password=doe
        """);

        var result = provisioningService.getProvisioningFile("aa-bb-cc");

        assertTrue(result.contains("username=john"));
        verify(deskGenerator).generate(anyMap());
    }

    @Test
    void testConferenceDeviceWithOverride() {
        var override = """
            {
              "domain": "sip.alt.com",
              "port": "9999",
              "timeout": 10
            }
            """;

        var device = createDevice(Device.DeviceModel.CONFERENCE, override);
        when(deviceRepository.findByMacAddress("11-22-33")).thenReturn(Optional.of(device));
        when(confGenerator.generate(anyMap())).thenReturn("""
            {
              "username": "john",
              "password": "doe",
              "domain": "sip.alt.com",
              "port": "9999",
              "codecs": ["G711", "G729"],
              "timeout": 10
            }
            """);

        var result = provisioningService.getProvisioningFile("11-22-33");

        assertTrue(result.contains("\"username\": \"john\""));
        verify(confGenerator).generate(anyMap());
    }

    @Test
    void testDeviceNotFound() {
        when(deviceRepository.findByMacAddress("not-found")).thenReturn(Optional.empty());

        var ex = assertThrows(IllegalArgumentException.class,
                () -> provisioningService.getProvisioningFile("not-found"));

        assertEquals("Device not found for MAC: not-found", ex.getMessage());
    }

    private Device createDevice(Device.DeviceModel model, String override) {
        var device = new Device();
        device.setMacAddress("dummy-mac");
        device.setModel(model);
        device.setUsername("john");
        device.setPassword("doe");
        device.setOverrideFragment(override);
        return device;
    }
}