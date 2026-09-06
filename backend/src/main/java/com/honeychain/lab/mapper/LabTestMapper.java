package com.honeychain.lab.mapper;

import com.honeychain.batch.entity.HoneyBatch;
import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.blockchain.dto.BlockchainRecordResponse;
import com.honeychain.hive.entity.Hive;
import com.honeychain.lab.dto.LabTestCreateRequest;
import com.honeychain.lab.dto.LabTestResponse;
import com.honeychain.lab.dto.PendingBatchResponse;
import com.honeychain.lab.entity.LabTest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LabTestMapper {

    public LabTest toEntity(String batchId, Long labUserId, LabTestCreateRequest request, String certificateUrl, LocalDateTime testedAt) {
        return new LabTest(
                batchId,
                labUserId,
                request.getPurityScore(),
                request.getResult(),
                certificateUrl,
                request.getRemarks(),
                testedAt
        );
    }

    public LabTestResponse toResponse(LabTest test, BlockchainRecordResponse blockchainRecord) {
        if (test == null) return null;

        return new LabTestResponse(
                test.getId(),
                test.getBatchId(),
                test.getLabUserId(),
                test.getPurityScore(),
                test.getResult(),
                test.getCertificateUrl(),
                test.getRemarks(),
                test.getTestedAt(),
                test.getCreatedAt(),
                blockchainRecord
        );
    }

    public PendingBatchResponse toPendingResponse(HoneyBatch batch, BeekeeperProfile profile, Hive hive) {
        String beekeeperName = profile != null ? profile.getName() : "Unknown Beekeeper";
        String village = profile != null ? profile.getVillage() : "Unknown Village";
        String hiveCode = hive != null ? hive.getHiveCode() : "HIVE-UNKNOWN";
        String clusterName = hive != null ? hive.getClusterName() : "Unknown Cluster";

        return new PendingBatchResponse(
                batch.getBatchId(),
                beekeeperName,
                village,
                hiveCode,
                clusterName,
                batch.getHarvestDate(),
                batch.getQuantityKg(),
                batch.getPhotoUrl(),
                batch.getUpdatedAt() != null ? batch.getUpdatedAt() : batch.getCreatedAt()
        );
    }
}
