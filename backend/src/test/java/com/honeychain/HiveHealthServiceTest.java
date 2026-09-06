package com.honeychain;

import com.honeychain.iot.config.IoTProperties;
import com.honeychain.iot.dto.HiveHealthResponse;
import com.honeychain.iot.entity.HiveHealthStatus;
import com.honeychain.iot.entity.HiveSensorData;
import com.honeychain.iot.service.HiveHealthService;
import com.honeychain.iot.service.IoTService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class HiveHealthServiceTest {

    @Autowired
    private HiveHealthService hiveHealthService;

    @Autowired
    private IoTService iotService;

    @Autowired
    private IoTProperties iotProperties;

    // ── Test 1: HEALTHY scenario ───────────────────────────────────────────────
    @Test
    @DisplayName("1. Sensor data within all healthy thresholds → HEALTHY status")
    void testHealthyScenario() {
        // Temp: 35°C (normal 30–37), Humidity: 58% (normal 45–70), Activity: 90 (healthy >=70)
        HiveSensorData data = makeSensorData(100L, "35.00", "58.00", 90);
        HiveHealthResponse result = evaluateDirectly(data);

        assertEquals(HiveHealthStatus.HEALTHY, result.getStatus());
        assertTrue(result.getMessage().toLowerCase().contains("normal") ||
                result.getMessage().toLowerCase().contains("well"),
                "HEALTHY message should convey normalcy, got: " + result.getMessage());
    }

    // ── Test 2: WATCH via high temperature ───────────────────────────────────
    @Test
    @DisplayName("2. Slightly high temperature alone → WATCH status")
    void testWatchTemperature() {
        // Temp: 38°C (watch: 37–39), Humidity: 60% (normal), Activity: 72 (healthy)
        HiveSensorData data = makeSensorData(101L, "38.00", "60.00", 72);
        HiveHealthResponse result = evaluateDirectly(data);

        assertEquals(HiveHealthStatus.WATCH, result.getStatus());
    }

    // ── Test 3: WATCH via reduced bee activity ────────────────────────────────
    @Test
    @DisplayName("3. Reduced bee activity (40–69) alone → WATCH status")
    void testWatchActivity() {
        // Temp: 35°C (normal), Humidity: 60% (normal), Activity: 63 (watch: 40–69)
        HiveSensorData data = makeSensorData(102L, "35.00", "60.00", 63);
        HiveHealthResponse result = evaluateDirectly(data);

        assertEquals(HiveHealthStatus.WATCH, result.getStatus());
    }

    // ── Test 4: ALERT via multiple abnormal values ────────────────────────────
    @Test
    @DisplayName("4. High temp + very low activity → ALERT status")
    void testAlertMultipleAbnormal() {
        // Temp: 40.2°C (alert >=39), Humidity: 74% (watch), Activity: 28 (alert <40)
        HiveSensorData data = makeSensorData(103L, "40.20", "74.00", 28);
        HiveHealthResponse result = evaluateDirectly(data);

        assertEquals(HiveHealthStatus.ALERT, result.getStatus());
        assertTrue(result.getMessage().contains("⚠️"),
                "ALERT message should contain warning symbol");
    }

    // ── Test 5: ALERT via dangerous temp alone is still WATCH (single-alert rule) ───
    @Test
    @DisplayName("5. Single ALERT dimension alone → WATCH (prefer WATCH over false ALERTs)")
    void testSingleAlertBecomesWatch() {
        // Only temp is ALERT (40°C), rest are normal
        HiveSensorData data = makeSensorData(104L, "40.00", "60.00", 75);
        HiveHealthResponse result = evaluateDirectly(data);

        // Single ALERT → WATCH (to avoid false positives per spec)
        assertEquals(HiveHealthStatus.WATCH, result.getStatus());
    }

    // ── Test 6: Critical humidity + alert activity → ALERT ────────────────────
    @Test
    @DisplayName("6. Critical humidity + critical activity → ALERT")
    void testAlertHumidityAndActivity() {
        // Humidity: 85% (alert >=80), Activity: 25 (alert <40), Temp: 35°C (normal)
        HiveSensorData data = makeSensorData(105L, "35.00", "85.00", 25);
        HiveHealthResponse result = evaluateDirectly(data);

        assertEquals(HiveHealthStatus.ALERT, result.getStatus());
    }

    // ── Test 7: Threshold boundary for temperature WATCH ─────────────────────
    @Test
    @DisplayName("7. Temperature exactly at watch threshold → WATCH")
    void testTemperatureBoundary() {
        double watchThreshold = iotProperties.getHealth().getTemperature().getWatchThreshold();
        HiveSensorData data = makeSensorData(106L,
                BigDecimal.valueOf(watchThreshold).toPlainString(), "60.00", 80);
        HiveHealthResponse result = evaluateDirectly(data);

        // At threshold → WATCH
        assertEquals(HiveHealthStatus.WATCH, result.getStatus());
    }

    // ── Test 8: Configuration properties loaded correctly ─────────────────────
    @Test
    @DisplayName("8. IoTProperties thresholds are loaded from application-test.properties")
    void testConfigurationLoaded() {
        assertEquals(37.0, iotProperties.getHealth().getTemperature().getWatchThreshold(), 0.01);
        assertEquals(39.0, iotProperties.getHealth().getTemperature().getAlertThreshold(), 0.01);
        assertEquals(70.0, iotProperties.getHealth().getHumidity().getWatchThreshold(), 0.01);
        assertEquals(80.0, iotProperties.getHealth().getHumidity().getAlertThreshold(), 0.01);
        assertEquals(70, iotProperties.getHealth().getActivity().getHealthyMin());
        assertEquals(40, iotProperties.getHealth().getActivity().getWatchMin());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private HiveSensorData makeSensorData(Long hiveId, String temp, String humidity, int activity) {
        return new HiveSensorData(
                hiveId,
                new BigDecimal(temp),
                new BigDecimal(humidity),
                activity,
                LocalDateTime.now()
        );
    }

    /**
     * Direct rule evaluation bypassing database lookup — tests the health engine in isolation.
     * We inject the hive health response through the service which calls IoTService.
     * For unit tests we use hiveId values that won't exist in DB,
     * so IoTService will generate a reading on demand.
     * Instead we bypass and call the service with a pre-seeded reading via a manual invocation.
     */
    private HiveHealthResponse evaluateDirectly(HiveSensorData data) {
        // For isolated tests: use IoTService.generateMockReading so a reading exists,
        // then assert on the health response for a seeded hive.
        // True unit isolation is in HiveHealthServiceTest which reaches the rule engine.
        // Here we store the data directly through IoTService and verify the result.

        // Save via repository directly through application context
        com.honeychain.iot.repository.HiveSensorDataRepository repo =
                ((com.honeychain.iot.service.impl.MockIoTServiceImpl) iotService) instanceof
                        com.honeychain.iot.service.IoTService
                        ? null : null;

        // Alternative: use HiveHealthServiceImpl's internal logic directly via test utility
        // Since HiveHealthServiceImpl is package-private in logic, we invoke via exposed service
        // For full isolated unit tests, see MockIoTServiceImplTest
        // Here we just verify the combined flow produces the right status

        com.honeychain.iot.repository.HiveSensorDataRepository sensorRepo =
                getBean(com.honeychain.iot.repository.HiveSensorDataRepository.class);
        sensorRepo.save(data);

        return hiveHealthService.analyzeHealth(data.getHiveId());
    }

    @Autowired
    private org.springframework.context.ApplicationContext applicationContext;

    private <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }
}
