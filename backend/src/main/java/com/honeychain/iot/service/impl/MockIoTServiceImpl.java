package com.honeychain.iot.service.impl;

import com.honeychain.hive.entity.Hive;
import com.honeychain.hive.repository.HiveRepository;
import com.honeychain.iot.config.IoTProperties;
import com.honeychain.iot.entity.HiveSensorData;
import com.honeychain.iot.repository.HiveSensorDataRepository;
import com.honeychain.iot.service.IoTService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Simulates IoT sensor readings for demo purposes.
 * Produces hive-seeded, realistic readings with controlled variance.
 * Occasionally generates anomalous values so that WATCH/ALERT states are
 * demonstrable in the hackathon demo.
 *
 * To replace with real IoT/MQTT: implement IoTService with an MQTT-based
 * provider and swap the Spring bean — HiveHealthService requires no changes.
 */
@Service
public class MockIoTServiceImpl implements IoTService {

    private static final Logger logger = LoggerFactory.getLogger(MockIoTServiceImpl.class);

    private final HiveSensorDataRepository sensorDataRepository;
    private final HiveRepository hiveRepository;
    private final IoTProperties iotProperties;
    private final Random random = new Random();

    public MockIoTServiceImpl(HiveSensorDataRepository sensorDataRepository,
            HiveRepository hiveRepository,
            IoTProperties iotProperties) {
        this.sensorDataRepository = sensorDataRepository;
        this.hiveRepository = hiveRepository;
        this.iotProperties = iotProperties;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<HiveSensorData> getLatestSensorData(Long hiveId) {
        return sensorDataRepository.findTop1ByHiveIdOrderByRecordedAtDesc(hiveId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HiveSensorData> getSensorHistory(Long hiveId, Pageable pageable) {
        return sensorDataRepository.findByHiveIdOrderByRecordedAtDesc(hiveId, pageable);
    }

    @Override
    @Transactional
    public HiveSensorData generateMockReading(Long hiveId) {
        // Use hiveId as a baseline seed for consistent per-hive personalities
        // Hive 1 → typically healthy, Hive 2 → borderline WATCH, Hive 3 → stress-prone
        long seed = hiveId % 3;

        BigDecimal temperature = generateTemperature(hiveId, seed);
        BigDecimal humidity = generateHumidity(hiveId, seed);
        int beeActivity = generateBeeActivity(hiveId, seed);

        HiveSensorData data = new HiveSensorData(hiveId, temperature, humidity, beeActivity, LocalDateTime.now());
        HiveSensorData saved = sensorDataRepository.save(data);

        logger.debug("Mock IoT reading generated for hive {}: temp={}°C, humidity={}%, activity={}",
                hiveId, temperature, humidity, beeActivity);

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public long countReadings(Long hiveId) {
        return sensorDataRepository.countByHiveId(hiveId);
    }

    /**
     * Scheduled sensor data generation for all active hives.
     * Interval is configured via iot.mock.interval-ms (default: 60 seconds).
     */
    @Scheduled(fixedDelayString = "${iot.mock.interval-ms:60000}")
    @Transactional
    public void scheduledSensorGeneration() {
        if (!iotProperties.getMock().isEnabled()) {
            return;
        }

        List<Hive> allHives = hiveRepository.findAll();
        if (allHives.isEmpty()) {
            return;
        }

        for (Hive hive : allHives) {
            try {
                generateMockReading(hive.getId());
            } catch (Exception e) {
                logger.error("Failed to generate mock sensor data for hive {}: {}", hive.getId(), e.getMessage());
            }
        }

        logger.info("Scheduled IoT sensor generation complete: {} hives processed", allHives.size());
    }

    // ── Private Generation Helpers ────────────────────────────────────────────

    private BigDecimal generateTemperature(Long hiveId, long seed) {
        double base;
        int anomalyChance = random.nextInt(10); // 0–9

        if (seed == 0) {
            // Hive "personality" 0 → typically healthy (32–36°C)
            base = 33.0 + random.nextDouble() * 3.0;
        } else if (seed == 1) {
            // Personality 1 → watch-prone (36–39°C)
            base = 36.0 + random.nextDouble() * 3.0;
            if (anomalyChance < 2)
                base += 2.0; // occasional ALERT spike
        } else {
            // Personality 2 → stress-prone (34–40°C)
            base = 34.0 + random.nextDouble() * 4.0;
            if (anomalyChance < 3)
                base += 3.5; // frequent ALERT spikes
        }

        // Small noise (±0.3°C)
        base += (random.nextDouble() - 0.5) * 0.6;
        return BigDecimal.valueOf(base).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal generateHumidity(Long hiveId, long seed) {
        double base;
        int anomalyChance = random.nextInt(10);

        if (seed == 0) {
            // Healthy: 50–65%
            base = 50.0 + random.nextDouble() * 15.0;
        } else if (seed == 1) {
            // Watch-prone: 65–75%
            base = 65.0 + random.nextDouble() * 10.0;
            if (anomalyChance < 2)
                base += 8.0;
        } else {
            // Stress-prone: 60–82%
            base = 60.0 + random.nextDouble() * 22.0;
            if (anomalyChance < 3)
                base += 5.0;
        }

        // Small noise (±1%)
        base += (random.nextDouble() - 0.5) * 2.0;
        // Clamp to [20, 100]
        base = Math.max(20.0, Math.min(100.0, base));
        return BigDecimal.valueOf(base).setScale(2, RoundingMode.HALF_UP);
    }

    private int generateBeeActivity(Long hiveId, long seed) {
        int base;
        int anomalyChance = random.nextInt(10);

        if (seed == 0) {
            // Healthy: 75–100
            base = 75 + random.nextInt(26);
        } else if (seed == 1) {
            // Watch-prone: 50–75
            base = 50 + random.nextInt(26);
            if (anomalyChance < 2)
                base -= 20; // occasional drop
        } else {
            // Stress-prone: 20–70
            base = 20 + random.nextInt(51);
            if (anomalyChance < 3)
                base -= 15; // frequent drops
        }

        // Clamp to [0, 100]
        return Math.max(0, Math.min(100, base));
    }
}
