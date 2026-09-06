package com.honeychain.iot.service;

import com.honeychain.iot.entity.HiveSensorData;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Abstraction for the IoT data source.
 *
 * Current implementation: MockIoTServiceImpl (simulated readings).
 * Future implementation: Real MQTT/ESP32 provider — swap implementation without
 * touching HiveHealthService or any frontend logic.
 */
public interface IoTService {

    /**
     * Retrieves the most recent sensor reading for a given hive.
     * Returns empty if no readings have been generated yet.
     */
    Optional<HiveSensorData> getLatestSensorData(Long hiveId);

    /**
     * Retrieves paginated sensor history for a given hive (newest first).
     */
    List<HiveSensorData> getSensorHistory(Long hiveId, Pageable pageable);

    /**
     * Generates and persists a new mock sensor reading for the given hive.
     * This method is called by the scheduler or on-demand for demo purposes.
     */
    HiveSensorData generateMockReading(Long hiveId);

    /**
     * Returns the count of stored readings for a hive.
     */
    long countReadings(Long hiveId);
}
