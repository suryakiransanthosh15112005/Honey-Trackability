package com.honeychain.iot.service;

import com.honeychain.iot.dto.HiveHealthResponse;

import java.util.List;

/**
 * Analyzes IoT sensor data and produces human-readable hive health assessments.
 * This service depends on IoTService (not MockIoTServiceImpl) for sensor data —
 * swapping the IoT provider does not require changes here.
 */
public interface HiveHealthService {

    /**
     * Analyzes the latest sensor data for a hive and returns a health assessment.
     * Ownership must be verified by the caller (controller) before calling this.
     */
    HiveHealthResponse analyzeHealth(Long hiveId);

    /**
     * Returns health assessments for all hives owned by the authenticated beekeeper.
     */
    List<HiveHealthResponse> analyzeAllHivesHealth(String beekeeperPhoneNumber);
}
