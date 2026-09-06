package com.honeychain.hive.mapper;

import com.honeychain.hive.dto.HiveCreateRequest;
import com.honeychain.hive.dto.HiveResponse;
import com.honeychain.hive.dto.HiveUpdateRequest;
import com.honeychain.hive.entity.Hive;
import com.honeychain.hive.entity.HiveStatus;
import org.springframework.stereotype.Component;

@Component
public class HiveMapper {

    public Hive toEntity(HiveCreateRequest request, Long beekeeperProfileId, String hiveCode) {
        return new Hive(
                beekeeperProfileId,
                hiveCode,
                request.getClusterName().trim(),
                request.getLatitude(),
                request.getLongitude(),
                HiveStatus.ACTIVE,
                request.getInstalledDate());
    }

    public void updateEntity(Hive hive, HiveUpdateRequest request) {
        hive.setClusterName(request.getClusterName().trim());
        hive.setLatitude(request.getLatitude());
        hive.setLongitude(request.getLongitude());
        hive.setInstalledDate(request.getInstalledDate());
    }

    public HiveResponse toResponse(Hive hive) {
        return new HiveResponse(
                hive.getId(),
                hive.getHiveCode(),
                hive.getClusterName(),
                hive.getLatitude(),
                hive.getLongitude(),
                hive.getStatus(),
                hive.getInstalledDate(),
                hive.getCreatedAt(),
                hive.getUpdatedAt());
    }
}
