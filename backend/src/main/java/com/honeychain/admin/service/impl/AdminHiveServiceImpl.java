package com.honeychain.admin.service.impl;

import com.honeychain.admin.dto.AdminHiveResponse;
import com.honeychain.admin.service.AdminHiveService;
import com.honeychain.beekeeper.repository.BeekeeperProfileRepository;
import com.honeychain.common.dto.PageResponse;
import com.honeychain.hive.entity.Hive;
import com.honeychain.hive.entity.HiveStatus;
import com.honeychain.hive.repository.HiveRepository;
import com.honeychain.iot.dto.HiveHealthResponse;
import com.honeychain.iot.entity.HiveHealthStatus;
import com.honeychain.iot.entity.HiveSensorData;
import com.honeychain.iot.repository.HiveSensorDataRepository;
import com.honeychain.iot.service.HiveHealthService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AdminHiveServiceImpl implements AdminHiveService {

    private final HiveRepository hiveRepository;
    private final BeekeeperProfileRepository beekeeperProfileRepository;
    private final HiveSensorDataRepository hiveSensorDataRepository;
    private final HiveHealthService hiveHealthService;

    public AdminHiveServiceImpl(HiveRepository hiveRepository,
            BeekeeperProfileRepository beekeeperProfileRepository,
            HiveSensorDataRepository hiveSensorDataRepository,
            HiveHealthService hiveHealthService) {
        this.hiveRepository = hiveRepository;
        this.beekeeperProfileRepository = beekeeperProfileRepository;
        this.hiveSensorDataRepository = hiveSensorDataRepository;
        this.hiveHealthService = hiveHealthService;
    }

    @Override
    public PageResponse<AdminHiveResponse> getHives(HiveStatus status, String search, Pageable pageable) {
        String cleanSearch = (search != null && !search.isBlank()) ? search.trim() : null;
        Page<Hive> page = hiveRepository.findAdminHives(status, cleanSearch, pageable);
        List<AdminHiveResponse> content = page.getContent().stream().map(this::toResponse).toList();
        return PageResponse.of(page, content);
    }

    private AdminHiveResponse toResponse(Hive hive) {
        AdminHiveResponse res = new AdminHiveResponse();
        res.setId(hive.getId());
        res.setHiveCode(hive.getHiveCode());
        res.setBeekeeperProfileId(hive.getBeekeeperProfileId());
        res.setClusterName(hive.getClusterName());
        res.setInstalledDate(hive.getInstalledDate());
        res.setLatitude(hive.getLatitude());
        res.setLongitude(hive.getLongitude());
        res.setStatus(hive.getStatus());

        beekeeperProfileRepository.findById(hive.getBeekeeperProfileId()).ifPresent(bp -> {
            res.setBeekeeperName(bp.getName());
            res.setVillage(bp.getVillage());
        });

        // Latest IoT Sensor Data
        Optional<HiveSensorData> latestData = hiveSensorDataRepository
                .findTop1ByHiveIdOrderByRecordedAtDesc(hive.getId());
        if (latestData.isPresent()) {
            HiveSensorData sensor = latestData.get();
            res.setLastTemperature(sensor.getTemperature() != null ? sensor.getTemperature().doubleValue() : null);
            res.setLastHumidity(sensor.getHumidity() != null ? sensor.getHumidity().doubleValue() : null);
            res.setLastBeeActivity(sensor.getBeeActivity());
            res.setLastSensorReadingTime(sensor.getRecordedAt());
        }

        // Hive Health Assessment
        try {
            HiveHealthResponse health = hiveHealthService.analyzeHealth(hive.getId());
            res.setHealthStatus(health != null ? health.getStatus() : HiveHealthStatus.HEALTHY);
        } catch (Exception e) {
            res.setHealthStatus(HiveHealthStatus.HEALTHY);
        }

        return res;
    }
}
