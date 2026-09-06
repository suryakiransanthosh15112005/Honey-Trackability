package com.honeychain.iot.entity;

/**
 * Health status of a hive derived from IoT sensor data analysis.
 *
 * This is SEPARATE from HiveStatus (ACTIVE/INACTIVE) which describes the hive's
 * operational lifecycle. HiveHealthStatus describes the bees' current wellbeing
 * based on real-time (or simulated) sensor readings.
 *
 * These thresholds are prototype heuristics for MVP demonstration purposes only,
 * and should not be interpreted as veterinary or scientific recommendations.
 */
public enum HiveHealthStatus {
    HEALTHY,
    WATCH,
    ALERT
}
