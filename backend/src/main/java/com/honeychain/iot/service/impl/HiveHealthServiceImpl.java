package com.honeychain.iot.service.impl;

import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.beekeeper.repository.BeekeeperProfileRepository;
import com.honeychain.hive.entity.Hive;
import com.honeychain.hive.repository.HiveRepository;
import com.honeychain.iot.config.IoTProperties;
import com.honeychain.iot.dto.HiveHealthResponse;
import com.honeychain.iot.entity.HiveHealthStatus;
import com.honeychain.iot.entity.HiveSensorData;
import com.honeychain.iot.service.HiveHealthService;
import com.honeychain.iot.service.IoTService;
import com.honeychain.notification.entity.NotificationType;
import com.honeychain.notification.event.NotificationEvent;
import com.honeychain.user.entity.User;
import com.honeychain.user.service.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Deterministic health rule engine based on IoTProperties thresholds.
 * Rules are deliberately simple and explainable — no ML, no probability.
 * Thresholds are configured externally, not hardcoded.
 *
 * IMPORTANT: These rules are prototype heuristics for demo purposes only.
 */
@Service
public class HiveHealthServiceImpl implements HiveHealthService {

    private final IoTService iotService;
    private final HiveRepository hiveRepository;
    private final BeekeeperProfileRepository beekeeperProfileRepository;
    private final UserService userService;
    private final IoTProperties iotProperties;
    private final ApplicationEventPublisher eventPublisher;

    // Deduplication cooldown map: hiveId -> lastAlertNotificationTimestamp
    private final Map<Long, LocalDateTime> lastAlertMap = new ConcurrentHashMap<>();

    public HiveHealthServiceImpl(IoTService iotService,
            HiveRepository hiveRepository,
            BeekeeperProfileRepository beekeeperProfileRepository,
            UserService userService,
            IoTProperties iotProperties) {
        this(iotService, hiveRepository, beekeeperProfileRepository, userService, iotProperties, event -> {
        });
    }

    @org.springframework.beans.factory.annotation.Autowired
    public HiveHealthServiceImpl(IoTService iotService,
            HiveRepository hiveRepository,
            BeekeeperProfileRepository beekeeperProfileRepository,
            UserService userService,
            IoTProperties iotProperties,
            ApplicationEventPublisher eventPublisher) {
        this.iotService = iotService;
        this.hiveRepository = hiveRepository;
        this.beekeeperProfileRepository = beekeeperProfileRepository;
        this.userService = userService;
        this.iotProperties = iotProperties;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional(readOnly = true)
    public HiveHealthResponse analyzeHealth(Long hiveId) {
        Hive hive = hiveRepository.findById(hiveId).orElse(null);

        Optional<HiveSensorData> latestOpt = iotService.getLatestSensorData(hiveId);

        if (latestOpt.isEmpty()) {
            // If no data yet, generate first reading on demand
            HiveSensorData generated = iotService.generateMockReading(hiveId);
            return buildHealthResponse(hive, generated);
        }

        return buildHealthResponse(hive, latestOpt.get());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HiveHealthResponse> analyzeAllHivesHealth(String beekeeperPhoneNumber) {
        User user = userService.findEntityByPhoneNumber(beekeeperPhoneNumber);
        BeekeeperProfile profile = beekeeperProfileRepository.findByUserId(user.getId()).orElse(null);

        if (profile == null) {
            return List.of();
        }

        List<Hive> hives = hiveRepository.findAllByBeekeeperProfileId(profile.getId());
        List<HiveHealthResponse> responses = new ArrayList<>();

        for (Hive hive : hives) {
            responses.add(analyzeHealth(hive.getId()));
        }

        return responses;
    }

    // ── Health Analysis Rule Engine ───────────────────────────────────────────

    private HiveHealthResponse buildHealthResponse(Hive hive, HiveSensorData data) {
        HiveHealthResponse response = new HiveHealthResponse();

        if (hive != null) {
            response.setHiveId(hive.getId());
            response.setHiveCode(hive.getHiveCode());
            response.setClusterName(hive.getClusterName());
        } else if (data != null) {
            response.setHiveId(data.getHiveId());
        }

        if (data == null) {
            response.setStatus(HiveHealthStatus.WATCH);
            response.setMessage("No sensor data available yet. Please check again shortly.");
            response.setNoDataAvailable(true);
            response.setCheckedAt(LocalDateTime.now());
            return response;
        }

        response.setTemperature(data.getTemperature());
        response.setHumidity(data.getHumidity());
        response.setBeeActivity(data.getBeeActivity());
        response.setCheckedAt(data.getRecordedAt());

        // Classify each sensor dimension
        SensorClassification tempClass = classifyTemperature(data.getTemperature().doubleValue());
        SensorClassification humClass = classifyHumidity(data.getHumidity().doubleValue());
        SensorClassification actClass = classifyActivity(data.getBeeActivity());

        // Combined rule: worst-case aggregation with preference for WATCH over false
        // ALERTs
        HiveHealthStatus status = aggregateStatus(tempClass, humClass, actClass);
        String message = buildMessage(tempClass, humClass, actClass, status);

        response.setStatus(status);
        response.setMessage(message);

        if (status == HiveHealthStatus.ALERT && hive != null) {
            LocalDateTime lastAlert = lastAlertMap.get(hive.getId());
            if (lastAlert == null || lastAlert.isBefore(LocalDateTime.now().minusHours(1))) {
                lastAlertMap.put(hive.getId(), LocalDateTime.now());
                BeekeeperProfile profile = beekeeperProfileRepository.findById(hive.getBeekeeperProfileId())
                        .orElse(null);
                if (profile != null) {
                    eventPublisher.publishEvent(new NotificationEvent(
                            profile.getUserId(),
                            "Hive Health Alert",
                            String.format("Hive %s requires attention: %s", hive.getHiveCode(), message),
                            NotificationType.HIVE_ALERT,
                            "HIVE",
                            hive.getId().toString(),
                            true));
                }
            }
        }

        return response;
    }

    private SensorClassification classifyTemperature(double temp) {
        IoTProperties.Health.Temperature cfg = iotProperties.getHealth().getTemperature();
        if (temp >= cfg.getAlertThreshold() || temp <= cfg.getLowerAlertThreshold())
            return SensorClassification.ALERT;
        if (temp >= cfg.getWatchThreshold() || temp <= cfg.getLowerWatchThreshold())
            return SensorClassification.WATCH;
        return SensorClassification.NORMAL;
    }

    private SensorClassification classifyHumidity(double humidity) {
        IoTProperties.Health.Humidity cfg = iotProperties.getHealth().getHumidity();
        if (humidity >= cfg.getAlertThreshold() || humidity <= cfg.getLowerAlertThreshold())
            return SensorClassification.ALERT;
        if (humidity >= cfg.getWatchThreshold() || humidity <= cfg.getLowerWatchThreshold())
            return SensorClassification.WATCH;
        return SensorClassification.NORMAL;
    }

    private SensorClassification classifyActivity(int activity) {
        IoTProperties.Health.Activity cfg = iotProperties.getHealth().getActivity();
        if (activity < cfg.getWatchMin())
            return SensorClassification.ALERT;
        if (activity < cfg.getHealthyMin())
            return SensorClassification.WATCH;
        return SensorClassification.NORMAL;
    }

    /**
     * Aggregation rules (deterministic, explainable):
     * - Any ALERT dimension + another ALERT or WATCH → ALERT
     * - Any single ALERT dimension → WATCH (avoid false positives)
     * - Any WATCH dimension, rest NORMAL → WATCH
     * - All NORMAL → HEALTHY
     */
    private HiveHealthStatus aggregateStatus(SensorClassification temp,
            SensorClassification humidity,
            SensorClassification activity) {
        long alertCount = countLevel(SensorClassification.ALERT, temp, humidity, activity);
        long watchCount = countLevel(SensorClassification.WATCH, temp, humidity, activity);

        if (alertCount >= 2)
            return HiveHealthStatus.ALERT;
        if (alertCount == 1 && watchCount >= 1)
            return HiveHealthStatus.ALERT;
        if (alertCount == 1)
            return HiveHealthStatus.WATCH; // single alert → prefer WATCH
        if (watchCount >= 1)
            return HiveHealthStatus.WATCH;
        return HiveHealthStatus.HEALTHY;
    }

    private long countLevel(SensorClassification level, SensorClassification... classifications) {
        long count = 0;
        for (SensorClassification c : classifications) {
            if (c == level)
                count++;
        }
        return count;
    }

    private String buildMessage(SensorClassification temp, SensorClassification humidity,
            SensorClassification activity, HiveHealthStatus status) {
        if (status == HiveHealthStatus.HEALTHY) {
            return "Temperature, humidity, and bee activity all look normal. Your hive is doing well.";
        }

        List<String> conditions = new ArrayList<>();

        if (temp == SensorClassification.ALERT)
            conditions.add("dangerous temperature levels");
        else if (temp == SensorClassification.WATCH)
            conditions.add("slightly high temperature");

        if (humidity == SensorClassification.ALERT)
            conditions.add("critical humidity levels");
        else if (humidity == SensorClassification.WATCH)
            conditions.add("elevated humidity");

        if (activity == SensorClassification.ALERT)
            conditions.add("very low bee activity");
        else if (activity == SensorClassification.WATCH)
            conditions.add("reduced bee activity");

        String conditionStr = String.join(" and ", conditions);

        if (status == HiveHealthStatus.ALERT) {
            return "⚠️ " + capitalize(conditionStr) + " detected. Please inspect the hive immediately.";
        } else {
            return capitalize(conditionStr) + " detected. Please check the hive.";
        }
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty())
            return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private enum SensorClassification {
        NORMAL, WATCH, ALERT
    }
}
