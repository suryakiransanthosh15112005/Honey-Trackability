package com.honeychain.batch.mapper;

import com.honeychain.batch.dto.HoneyBatchCreateRequest;
import com.honeychain.batch.dto.HoneyBatchResponse;
import com.honeychain.batch.dto.HoneyBatchUpdateRequest;
import com.honeychain.batch.entity.BatchStatus;
import com.honeychain.batch.entity.HoneyBatch;
import com.honeychain.hive.entity.Hive;
import org.springframework.stereotype.Component;

@Component
public class HoneyBatchMapper {

    public HoneyBatch toEntity(HoneyBatchCreateRequest request, Long beekeeperProfileId, String batchId, String photoUrl) {
        return new HoneyBatch(
                batchId,
                beekeeperProfileId,
                request.getHiveId(),
                request.getHarvestDate(),
                request.getQuantityKg(),
                photoUrl,
                BatchStatus.CREATED
        );
    }

    public void updateEntity(HoneyBatch batch, HoneyBatchUpdateRequest request, String photoUrl) {
        batch.setHarvestDate(request.getHarvestDate());
        batch.setQuantityKg(request.getQuantityKg());
        if (photoUrl != null) {
            batch.setPhotoUrl(photoUrl);
        }
    }

    public HoneyBatchResponse toResponse(HoneyBatch batch, Hive hive) {
        String hiveCode = hive != null ? hive.getHiveCode() : null;
        String clusterName = hive != null ? hive.getClusterName() : null;

        return new HoneyBatchResponse(
                batch.getId(),
                batch.getBatchId(),
                batch.getHiveId(),
                hiveCode,
                clusterName,
                batch.getHarvestDate(),
                batch.getQuantityKg(),
                batch.getPhotoUrl(),
                batch.getStatus(),
                batch.getCreatedAt(),
                batch.getUpdatedAt()
        );
    }
}
