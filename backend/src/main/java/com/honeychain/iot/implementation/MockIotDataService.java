package com.honeychain.iot.implementation;

import com.honeychain.iot.service.IotDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MockIotDataService implements IotDataService {

    private static final Logger logger = LoggerFactory.getLogger(MockIotDataService.class);

    @Override
    public void ingestTelemetry(String deviceId, Map<String, Object> metrics) {
        logger.info("[MockIoT] Ingested telemetry from device {}: {}", deviceId, metrics);
    }

    @Override
    public Map<String, Object> getLatestMetrics(String hiveId) {
        return Map.of(
                "hiveId", hiveId,
                "temperature", 34.5,
                "humidity", 58.2,
                "weightKg", 24.8,
                "status", "HEALTHY");
    }
}
