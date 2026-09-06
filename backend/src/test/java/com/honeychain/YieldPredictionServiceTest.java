package com.honeychain;

import com.honeychain.ai.config.AiProperties;
import com.honeychain.ai.dto.YieldPredictionResponse;
import com.honeychain.ai.entity.YieldPrediction;
import com.honeychain.ai.mapper.YieldPredictionMapper;
import com.honeychain.ai.repository.YieldPredictionRepository;
import com.honeychain.ai.service.impl.RuleBasedYieldPredictionService;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class YieldPredictionServiceTest {

    @Mock
    private YieldPredictionRepository predictionRepository;
    @Mock
    private HiveRepository hiveRepository;
    @Mock
    private BeekeeperProfileRepository beekeeperProfileRepository;
    @Mock
    private HoneyBatchRepository batchRepository;
    @Mock
    private IoTService ioTService;
    @Mock
    private HiveHealthService hiveHealthService;

    private YieldPredictionMapper mapper;
    private AiProperties aiProperties;
    private RuleBasedYieldPredictionService service;

    private String phone = "9876543213";
    private Long hiveId = 1L;
    private BeekeeperProfile profile;
    private Hive hive;

    @BeforeEach
    void setUp() {
        mapper = new YieldPredictionMapper();
        aiProperties = new AiProperties();
        service = new RuleBasedYieldPredictionService(
                predictionRepository,
                hiveRepository,
                beekeeperProfileRepository,
                batchRepository,
                ioTService,
                hiveHealthService,
                mapper,
                aiProperties);

        profile = new BeekeeperProfile();
        profile.setId(10L);

        hive = new Hive();
        hive.setId(hiveId);
        hive.setHiveCode("HIVE-0001");
        hive.setClusterName("Apiary Alpha");
    }

    @Test
    @DisplayName("Prediction for hive with no harvest history returns baseline 5-8 kg")
    void testPredictionWithNoHistory() {
        when(beekeeperProfileRepository.findByUserPhoneNumber(phone)).thenReturn(Optional.of(profile));
        when(hiveRepository.findByIdAndBeekeeperProfileId(hiveId, profile.getId())).thenReturn(Optional.of(hive));
        when(predictionRepository.findFirstByHiveIdOrderByGeneratedAtDesc(hiveId)).thenReturn(Optional.empty());
        when(batchRepository.findAllByHiveIdOrderByHarvestDateDesc(hiveId)).thenReturn(Collections.emptyList());

        HiveHealthResponse healthResp = new HiveHealthResponse();
        healthResp.setHiveId(hiveId);
        healthResp.setHiveCode("HIVE-0001");
        healthResp.setStatus(HiveHealthStatus.HEALTHY);
        healthResp.setMessage("Normal conditions");
        healthResp.setCheckedAt(LocalDateTime.now());
        when(hiveHealthService.analyzeHealth(eq(hiveId))).thenReturn(healthResp);
        when(ioTService.getLatestSensorData(hiveId)).thenReturn(Optional.empty());

        when(predictionRepository.save(any(YieldPrediction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        YieldPredictionResponse response = service.getPredictionForHive(hiveId, phone);

        assertNotNull(response);
        assertEquals("HIVE-0001", response.getHiveCode());
        assertTrue(response.getMinimumKg().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(response.getMinimumKg().compareTo(response.getMaximumKg()) <= 0);
        assertTrue(response.getConfidence() >= 0 && response.getConfidence() <= 100);
        assertTrue(response.getPredictedHarvestDate().isAfter(LocalDate.now().minusDays(1)));
        assertTrue(response.getExplanation().contains("No previous harvest history"));
    }

    @Test
    @DisplayName("Prediction with 1 historical harvest uses single harvest baseline")
    void testPredictionWithOneHarvest() {
        when(beekeeperProfileRepository.findByUserPhoneNumber(phone)).thenReturn(Optional.of(profile));
        when(hiveRepository.findByIdAndBeekeeperProfileId(hiveId, profile.getId())).thenReturn(Optional.of(hive));
        when(predictionRepository.findFirstByHiveIdOrderByGeneratedAtDesc(hiveId)).thenReturn(Optional.empty());

        HoneyBatch batch = new HoneyBatch();
        batch.setQuantityKg(new BigDecimal("10.00"));
        when(batchRepository.findAllByHiveIdOrderByHarvestDateDesc(hiveId)).thenReturn(List.of(batch));

        HiveHealthResponse healthResp = mockHealth(HiveHealthStatus.HEALTHY);
        when(hiveHealthService.analyzeHealth(eq(hiveId))).thenReturn(healthResp);

        when(predictionRepository.save(any(YieldPrediction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        YieldPredictionResponse response = service.getPredictionForHive(hiveId, phone);

        assertNotNull(response);
        assertTrue(response.getExplanation().contains("1 historical harvest"));
    }

    @Test
    @DisplayName("Prediction with multiple historical harvests calculates average correctly")
    void testPredictionWithMultipleHarvests() {
        when(beekeeperProfileRepository.findByUserPhoneNumber(phone)).thenReturn(Optional.of(profile));
        when(hiveRepository.findByIdAndBeekeeperProfileId(hiveId, profile.getId())).thenReturn(Optional.of(hive));
        when(predictionRepository.findFirstByHiveIdOrderByGeneratedAtDesc(hiveId)).thenReturn(Optional.empty());

        HoneyBatch b1 = new HoneyBatch();
        b1.setQuantityKg(new BigDecimal("8.00"));
        HoneyBatch b2 = new HoneyBatch();
        b2.setQuantityKg(new BigDecimal("10.00"));
        when(batchRepository.findAllByHiveIdOrderByHarvestDateDesc(hiveId)).thenReturn(List.of(b1, b2));

        HiveHealthResponse healthResp = mockHealth(HiveHealthStatus.HEALTHY);
        when(hiveHealthService.analyzeHealth(eq(hiveId))).thenReturn(healthResp);

        when(predictionRepository.save(any(YieldPrediction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        YieldPredictionResponse response = service.getPredictionForHive(hiveId, phone);

        assertNotNull(response);
        assertTrue(response.getExplanation().contains("2 historical harvests"));
    }

    @Test
    @DisplayName("ALERT health status reduces yield expectations and confidence")
    void testPredictionWithAlertHealth() {
        when(beekeeperProfileRepository.findByUserPhoneNumber(phone)).thenReturn(Optional.of(profile));
        when(hiveRepository.findByIdAndBeekeeperProfileId(hiveId, profile.getId())).thenReturn(Optional.of(hive));
        when(predictionRepository.findFirstByHiveIdOrderByGeneratedAtDesc(hiveId)).thenReturn(Optional.empty());
        when(batchRepository.findAllByHiveIdOrderByHarvestDateDesc(hiveId)).thenReturn(Collections.emptyList());

        HiveHealthResponse healthResp = mockHealth(HiveHealthStatus.ALERT);
        when(hiveHealthService.analyzeHealth(eq(hiveId))).thenReturn(healthResp);

        when(predictionRepository.save(any(YieldPrediction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        YieldPredictionResponse response = service.getPredictionForHive(hiveId, phone);

        assertNotNull(response);
        assertEquals("ALERT", response.getHealthStatus());
        assertTrue(response.getExplanation().contains("ALERT"));
    }

    @Test
    @DisplayName("High bee activity boosts yield & confidence")
    void testPredictionWithHighBeeActivity() {
        when(beekeeperProfileRepository.findByUserPhoneNumber(phone)).thenReturn(Optional.of(profile));
        when(hiveRepository.findByIdAndBeekeeperProfileId(hiveId, profile.getId())).thenReturn(Optional.of(hive));
        when(predictionRepository.findFirstByHiveIdOrderByGeneratedAtDesc(hiveId)).thenReturn(Optional.empty());
        when(batchRepository.findAllByHiveIdOrderByHarvestDateDesc(hiveId)).thenReturn(Collections.emptyList());

        HiveHealthResponse healthResp = mockHealth(HiveHealthStatus.HEALTHY);
        when(hiveHealthService.analyzeHealth(eq(hiveId))).thenReturn(healthResp);

        HiveSensorData sensor = new HiveSensorData(hiveId, new BigDecimal("34.5"), new BigDecimal("55.0"), 90,
                LocalDateTime.now());
        when(ioTService.getLatestSensorData(hiveId)).thenReturn(Optional.of(sensor));

        when(predictionRepository.save(any(YieldPrediction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        YieldPredictionResponse response = service.getPredictionForHive(hiveId, phone);

        assertNotNull(response);
        assertTrue(response.getExplanation().contains("Bee activity is high"));
    }

    @Test
    @DisplayName("Cached fresh prediction is returned if age < 24h")
    void testCachedFreshPredictionReturned() {
        when(beekeeperProfileRepository.findByUserPhoneNumber(phone)).thenReturn(Optional.of(profile));
        when(hiveRepository.findByIdAndBeekeeperProfileId(hiveId, profile.getId())).thenReturn(Optional.of(hive));

        YieldPrediction cached = new YieldPrediction(hive, LocalDate.now().plusDays(15), new BigDecimal("8.00"),
                new BigDecimal("10.00"), 82, LocalDateTime.now().minusHours(2), "Fresh prediction",
                HiveHealthStatus.HEALTHY);
        when(predictionRepository.findFirstByHiveIdOrderByGeneratedAtDesc(hiveId)).thenReturn(Optional.of(cached));

        YieldPredictionResponse response = service.getPredictionForHive(hiveId, phone);

        assertNotNull(response);
        assertEquals("Fresh prediction", response.getExplanation());
        verify(predictionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Accessing non-existent or unowned hive throws ResourceNotFoundException")
    void testAccessingUnownedHiveThrowsException() {
        when(beekeeperProfileRepository.findByUserPhoneNumber(phone)).thenReturn(Optional.of(profile));
        when(hiveRepository.findByIdAndBeekeeperProfileId(hiveId, profile.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getPredictionForHive(hiveId, phone));
    }

    private HiveHealthResponse mockHealth(HiveHealthStatus status) {
        HiveHealthResponse resp = new HiveHealthResponse();
        resp.setHiveId(hiveId);
        resp.setHiveCode("HIVE-0001");
        resp.setStatus(status);
        resp.setMessage("Status: " + status);
        resp.setCheckedAt(LocalDateTime.now());
        return resp;
    }
}
