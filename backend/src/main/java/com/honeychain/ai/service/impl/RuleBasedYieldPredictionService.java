package com.honeychain.ai.service.impl;

import com.honeychain.ai.config.AiProperties;
import com.honeychain.ai.dto.YieldPredictionExplanation;
import com.honeychain.ai.dto.YieldPredictionResponse;
import com.honeychain.ai.entity.YieldPrediction;
import com.honeychain.ai.mapper.YieldPredictionMapper;
import com.honeychain.ai.repository.YieldPredictionRepository;
import com.honeychain.ai.service.YieldPredictionService;
import com.honeychain.batch.entity.HoneyBatch;
import com.honeychain.batch.repository.HoneyBatchRepository;
import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.beekeeper.repository.BeekeeperProfileRepository;
import com.honeychain.common.exception.ResourceNotFoundException;
import com.honeychain.hive.entity.Hive;
import com.honeychain.hive.repository.HiveRepository;
import com.honeychain.iot.dto.HiveHealthResponse;
import com.honeychain.iot.entity.HiveHealthStatus;
import com.honeychain.iot.entity.HiveSensorData;
import com.honeychain.iot.service.HiveHealthService;
import com.honeychain.iot.service.IoTService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RuleBasedYieldPredictionService implements YieldPredictionService {

    private final YieldPredictionRepository predictionRepository;
    private final HiveRepository hiveRepository;
    private final BeekeeperProfileRepository beekeeperProfileRepository;
    private final HoneyBatchRepository batchRepository;
    private final IoTService ioTService;
    private final HiveHealthService hiveHealthService;
    private final YieldPredictionMapper mapper;
    private final AiProperties aiProperties;

    public RuleBasedYieldPredictionService(YieldPredictionRepository predictionRepository,
                                            HiveRepository hiveRepository,
                                            BeekeeperProfileRepository beekeeperProfileRepository,
                                            HoneyBatchRepository batchRepository,
                                            IoTService ioTService,
                                            HiveHealthService hiveHealthService,
                                            YieldPredictionMapper mapper,
                                            AiProperties aiProperties) {
        this.predictionRepository = predictionRepository;
        this.hiveRepository = hiveRepository;
        this.beekeeperProfileRepository = beekeeperProfileRepository;
        this.batchRepository = batchRepository;
        this.ioTService = ioTService;
        this.hiveHealthService = hiveHealthService;
        this.mapper = mapper;
        this.aiProperties = aiProperties;
    }

    @Override
    @Transactional(readOnly = true)
    public YieldPredictionResponse getPredictionForHive(Long hiveId, String beekeeperPhone) {
        Hive hive = validateAndGetHive(hiveId, beekeeperPhone);

        // Check freshness window
        Optional<YieldPrediction> latestOpt = predictionRepository.findFirstByHiveIdOrderByGeneratedAtDesc(hiveId);
        if (latestOpt.isPresent()) {
            YieldPrediction latest = latestOpt.get();
            LocalDateTime threshold = LocalDateTime.now().minusHours(aiProperties.getMaxAgeHours());
            if (latest.getGeneratedAt().isAfter(threshold)) {
                YieldPredictionExplanation details = buildExplanationDetails(hive, latest);
                return mapper.toResponse(latest, details);
            }
        }

        // Otherwise generate fresh prediction
        return generateAndSavePrediction(hive, beekeeperPhone);
    }

    @Override
    @Transactional(readOnly = true)
    public List<YieldPredictionResponse> getAllPredictionsForBeekeeper(String beekeeperPhone) {
        Optional<BeekeeperProfile> profileOpt = beekeeperProfileRepository.findByUserPhoneNumber(beekeeperPhone);
        if (profileOpt.isEmpty()) {
            return List.of();
        }
        BeekeeperProfile profile = profileOpt.get();

        List<Hive> hives = hiveRepository.findAllByBeekeeperProfileId(profile.getId());
        List<YieldPredictionResponse> responses = new ArrayList<>();
        for (Hive hive : hives) {
            try {
                responses.add(getPredictionForHive(hive.getId(), beekeeperPhone));
            } catch (Exception ignored) {
                // Return prediction for available hives
            }
        }
        return responses;
    }

    @Override
    public YieldPredictionResponse refreshPredictionForHive(Long hiveId, String beekeeperPhone) {
        Hive hive = validateAndGetHive(hiveId, beekeeperPhone);
        return generateAndSavePrediction(hive, beekeeperPhone);
    }

    private YieldPredictionResponse generateAndSavePrediction(Hive hive, String beekeeperPhone) {
        // 1. Fetch historical batches
        List<HoneyBatch> batches = batchRepository.findAllByHiveIdOrderByHarvestDateDesc(hive.getId());

        // 2. Fetch hive health
        HiveHealthResponse healthResponse = hiveHealthService.analyzeHealth(hive.getId());
        HiveHealthStatus healthStatus = healthResponse.getStatus();

        // 3. Fetch latest IoT telemetry
        Optional<HiveSensorData> sensorOpt = ioTService.getLatestSensorData(hive.getId());

        // Base calculation logic
        BigDecimal baseMin;
        BigDecimal baseMax;
        BigDecimal avgYield = BigDecimal.ZERO;
        int dataPoints = batches.size();
        int baseConfidence;
        StringBuilder explanationBuilder = new StringBuilder();

        if (batches.isEmpty()) {
            baseMin = aiProperties.getDefaultMinKg();
            baseMax = aiProperties.getDefaultMaxKg();
            baseConfidence = 50;
            explanationBuilder.append("No previous harvest history is available for this hive, so the estimate uses default baseline parameters. ");
        } else if (batches.size() == 1) {
            BigDecimal singleYield = batches.get(0).getQuantityKg();
            avgYield = singleYield;
            baseMin = singleYield.multiply(new BigDecimal("0.90"));
            baseMax = singleYield.multiply(new BigDecimal("1.10"));
            baseConfidence = 65;
            explanationBuilder.append(String.format("Estimate baseline is calculated from 1 historical harvest (%.1f kg). ", singleYield));
        } else {
            BigDecimal totalKg = BigDecimal.ZERO;
            for (HoneyBatch b : batches) {
                totalKg = totalKg.add(b.getQuantityKg());
            }
            avgYield = totalKg.divide(BigDecimal.valueOf(batches.size()), 2, RoundingMode.HALF_UP);
            baseMin = avgYield.multiply(new BigDecimal("0.92"));
            baseMax = avgYield.multiply(new BigDecimal("1.08"));
            baseConfidence = Math.min(90, 75 + (batches.size() * 2));
            explanationBuilder.append(String.format("Estimate baseline is derived from %d historical harvests (average %.1f kg). ", batches.size(), avgYield));
        }

        // Adjust for Health Status
        BigDecimal healthMultiplier;
        int harvestDays;
        switch (healthStatus) {
            case HEALTHY -> {
                healthMultiplier = aiProperties.getHealthyAdjustment();
                harvestDays = aiProperties.getHealthyDays();
                explanationBuilder.append("Hive health is HEALTHY, which positively supports yield potential. ");
            }
            case WATCH -> {
                healthMultiplier = aiProperties.getWatchAdjustment();
                harvestDays = aiProperties.getWatchDays();
                baseConfidence -= 10;
                explanationBuilder.append("Hive is under WATCH status due to environmental variance; prediction is adjusted downward. ");
            }
            case ALERT -> {
                healthMultiplier = aiProperties.getAlertAdjustment();
                harvestDays = aiProperties.getAlertDays();
                baseConfidence -= 25;
                explanationBuilder.append("Hive is in ALERT status; prediction confidence is reduced and lower yield is expected. ");
            }
            default -> {
                healthMultiplier = BigDecimal.ONE;
                harvestDays = 15;
            }
        }

        // Adjust for Bee Activity & Sensors
        Integer activity = null;
        BigDecimal temp = null;
        BigDecimal hum = null;
        if (sensorOpt.isPresent()) {
            HiveSensorData sensor = sensorOpt.get();
            activity = sensor.getBeeActivity();
            temp = sensor.getTemperature();
            hum = sensor.getHumidity();

            if (activity >= 80) {
                healthMultiplier = healthMultiplier.multiply(new BigDecimal("1.05"));
                baseConfidence += 5;
                explanationBuilder.append("Bee activity is high (>=80), boosting yield confidence. ");
            } else if (activity < 40) {
                healthMultiplier = healthMultiplier.multiply(new BigDecimal("0.85"));
                baseConfidence -= 10;
                explanationBuilder.append("Bee activity is lower than normal (<40), reducing expected yield. ");
            }
        }

        // Final Bounds & Range Calculations
        BigDecimal finalMin = baseMin.multiply(healthMultiplier).setScale(2, RoundingMode.HALF_UP);
        BigDecimal finalMax = baseMax.multiply(healthMultiplier).setScale(2, RoundingMode.HALF_UP);

        // Safety Bounds: min > 0, max > min
        if (finalMin.compareTo(new BigDecimal("1.00")) < 0) {
            finalMin = new BigDecimal("1.00");
        }
        if (finalMax.compareTo(finalMin.add(new BigDecimal("0.50"))) < 0) {
            finalMax = finalMin.add(new BigDecimal("2.00")).setScale(2, RoundingMode.HALF_UP);
        }

        // Clamp confidence 0 - 100
        int finalConfidence = Math.max(0, Math.min(100, baseConfidence));

        LocalDate predictedHarvestDate = LocalDate.now().plusDays(harvestDays);
        LocalDateTime generatedAt = LocalDateTime.now();

        // Create & Persist Entity
        YieldPrediction prediction = new YieldPrediction(
                hive,
                predictedHarvestDate,
                finalMin,
                finalMax,
                finalConfidence,
                generatedAt,
                explanationBuilder.toString().trim(),
                healthStatus
        );

        YieldPrediction saved = predictionRepository.save(prediction);

        YieldPredictionExplanation explanationDetails = new YieldPredictionExplanation(
                dataPoints,
                avgYield,
                healthStatus.name(),
                activity,
                temp,
                hum,
                healthMultiplier.setScale(2, RoundingMode.HALF_UP),
                saved.getExplanation()
        );

        return mapper.toResponse(saved, explanationDetails);
    }

    private Hive validateAndGetHive(Long hiveId, String beekeeperPhone) {
        BeekeeperProfile profile = beekeeperProfileRepository.findByUserPhoneNumber(beekeeperPhone)
                .orElseThrow(() -> new ResourceNotFoundException("Beekeeper profile not found for phone: " + beekeeperPhone));

        return hiveRepository.findByIdAndBeekeeperProfileId(hiveId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Hive not found or access denied for hive ID: " + hiveId));
    }

    private YieldPredictionExplanation buildExplanationDetails(Hive hive, YieldPrediction prediction) {
        List<HoneyBatch> batches = batchRepository.findAllByHiveIdOrderByHarvestDateDesc(hive.getId());
        int dataPoints = batches.size();
        BigDecimal avgYield = BigDecimal.ZERO;
        if (dataPoints > 0) {
            BigDecimal totalKg = BigDecimal.ZERO;
            for (HoneyBatch b : batches) {
                totalKg = totalKg.add(b.getQuantityKg());
            }
            avgYield = totalKg.divide(BigDecimal.valueOf(dataPoints), 2, RoundingMode.HALF_UP);
        }

        Optional<HiveSensorData> sensorOpt = ioTService.getLatestSensorData(hive.getId());
        Integer activity = sensorOpt.map(HiveSensorData::getBeeActivity).orElse(null);
        BigDecimal temp = sensorOpt.map(HiveSensorData::getTemperature).orElse(null);
        BigDecimal hum = sensorOpt.map(HiveSensorData::getHumidity).orElse(null);

        return new YieldPredictionExplanation(
                dataPoints,
                avgYield,
                prediction.getHealthStatus().name(),
                activity,
                temp,
                hum,
                new BigDecimal("1.00"),
                prediction.getExplanation()
        );
    }
}
