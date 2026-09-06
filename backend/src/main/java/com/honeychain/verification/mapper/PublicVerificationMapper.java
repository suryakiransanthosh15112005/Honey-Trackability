package com.honeychain.verification.mapper;

import com.honeychain.batch.entity.HoneyBatch;
import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.blockchain.entity.BlockchainRecord;
import com.honeychain.hive.entity.Hive;
import com.honeychain.lab.entity.LabTest;
import com.honeychain.qr.entity.QrCode;
import com.honeychain.verification.dto.BlockchainVerificationSummary;
import com.honeychain.verification.dto.PublicVerificationResponse;
import com.honeychain.verification.dto.VerificationTimelineItem;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class PublicVerificationMapper {

    public PublicVerificationResponse toNotFoundResponse(String batchId) {
        PublicVerificationResponse response = new PublicVerificationResponse();
        response.setVerified(false);
        response.setVerificationStatus("NOT_FOUND");
        response.setMessage("This batch could not be verified.");
        response.setBatchId(batchId);
        return response;
    }

    public PublicVerificationResponse toResponse(HoneyBatch batch,
            BeekeeperProfile profile,
            Hive hive,
            LabTest labTest,
            BlockchainRecord batchBcRecord,
            BlockchainRecord labBcRecord,
            QrCode qrCode,
            boolean integrityVerified,
            String verificationStatus,
            String message,
            PublicVerificationResponse.VerificationHistorySummary historySummary) {
        PublicVerificationResponse res = new PublicVerificationResponse();
        res.setBatchId(batch.getBatchId());
        res.setVerified(integrityVerified && "GENUINE".equals(verificationStatus));
        res.setVerificationStatus(verificationStatus);
        res.setMessage(message);
        res.setHarvestDate(batch.getHarvestDate());
        res.setQuantityKg(batch.getQuantityKg());
        res.setBatchPhotoUrl(batch.getPhotoUrl());

        if (profile != null) {
            res.setBeekeeper(new PublicVerificationResponse.BeekeeperPublicInfo(
                    profile.getName(),
                    profile.getVillage(),
                    profile.getPhotoUrl()));
        }

        if (hive != null) {
            res.setHiveCode(hive.getHiveCode());
            res.setClusterName(hive.getClusterName());
        }

        if (labTest != null) {
            res.setPurity(new PublicVerificationResponse.PurityPublicInfo(
                    labTest.getPurityScore(),
                    labTest.getResult().name(),
                    labTest.getCertificateUrl(),
                    labTest.getRemarks(),
                    labTest.getTestedAt()));
        }

        // Blockchain Summary (prefer lab record if available, else batch created
        // record)
        BlockchainRecord primaryBcRecord = labBcRecord != null ? labBcRecord : batchBcRecord;
        if (primaryBcRecord != null) {
            res.setBlockchain(new BlockchainVerificationSummary(
                    integrityVerified,
                    primaryBcRecord.getNetwork(),
                    primaryBcRecord.getTransactionHash(),
                    primaryBcRecord.getBlockNumber(),
                    primaryBcRecord.getDataHash(),
                    primaryBcRecord.getRecordedAt()));
        }

        if (qrCode != null) {
            res.setQrGeneratedAt(qrCode.getGeneratedAt());
        }

        // Verification History Summary
        res.setVerificationHistory(historySummary);

        // Build Supply Chain Lifecycle Timeline
        res.setTimeline(buildTimeline(batch, hive, labTest, primaryBcRecord, qrCode));

        return res;
    }

    private List<VerificationTimelineItem> buildTimeline(HoneyBatch batch, Hive hive, LabTest labTest,
            BlockchainRecord bcRecord, QrCode qrCode) {
        List<VerificationTimelineItem> items = new ArrayList<>();

        // 1. Harvested
        LocalDateTime harvestTime = batch.getHarvestDate() != null ? batch.getHarvestDate().atStartOfDay()
                : batch.getCreatedAt();
        String hiveLabel = hive != null ? hive.getHiveCode() : "Registered Hive";
        items.add(new VerificationTimelineItem(
                "Honey Harvested",
                harvestTime,
                "COMPLETED",
                String.format("Harvested %s KG from %s", batch.getQuantityKg(), hiveLabel),
                "🍯"));

        // 2. Sent for Testing
        items.add(new VerificationTimelineItem(
                "Submitted for Lab Analysis",
                batch.getCreatedAt(),
                "COMPLETED",
                "Sample received and registered for laboratory quality inspection",
                "📤"));

        // 3. Lab Tested
        if (labTest != null) {
            items.add(new VerificationTimelineItem(
                    "Laboratory Purity Analysis",
                    labTest.getTestedAt(),
                    labTest.getResult().name(),
                    String.format("Purity score: %d%% — Result: %s", labTest.getPurityScore(), labTest.getResult()),
                    "🧪"));
        }

        // 4. Blockchain Recorded
        if (bcRecord != null) {
            items.add(new VerificationTimelineItem(
                    "Blockchain Immutable Record",
                    bcRecord.getRecordedAt(),
                    "VERIFIED",
                    String.format("Block #%d on %s (Tx: %s...)",
                            bcRecord.getBlockNumber(),
                            bcRecord.getNetwork(),
                            bcRecord.getTransactionHash().substring(0,
                                    Math.min(10, bcRecord.getTransactionHash().length()))),
                    "🔗"));
        }

        // 5. QR Passport Generated
        if (qrCode != null) {
            items.add(new VerificationTimelineItem(
                    "QR Code Passport Activated",
                    qrCode.getGeneratedAt(),
                    "ACTIVE",
                    "Authenticity passport generated for consumer verification",
                    "📱"));
        }

        return items;
    }
}
