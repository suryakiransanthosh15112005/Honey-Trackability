package com.honeychain.admin.service.impl;

import com.honeychain.admin.dto.AdminBatchDetailsResponse;
import com.honeychain.admin.dto.AdminBatchResponse;
import com.honeychain.admin.service.AdminBatchService;
import com.honeychain.batch.entity.BatchStatus;
import com.honeychain.batch.entity.HoneyBatch;
import com.honeychain.batch.repository.HoneyBatchRepository;
import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.beekeeper.repository.BeekeeperProfileRepository;
import com.honeychain.blockchain.entity.BlockchainRecord;
import com.honeychain.blockchain.entity.BlockchainRecordType;
import com.honeychain.blockchain.repository.BlockchainRecordRepository;
import com.honeychain.common.dto.PageResponse;
import com.honeychain.common.exception.ResourceNotFoundException;
import com.honeychain.hive.entity.Hive;
import com.honeychain.hive.repository.HiveRepository;
import com.honeychain.lab.entity.LabTest;
import com.honeychain.lab.repository.LabTestRepository;
import com.honeychain.marketplace.repository.ProductRepository;
import com.honeychain.qr.repository.QrCodeRepository;
import com.honeychain.verification.entity.VerificationHistory;
import com.honeychain.verification.entity.VerificationRiskLevel;
import com.honeychain.verification.repository.VerificationHistoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AdminBatchServiceImpl implements AdminBatchService {

    private final HoneyBatchRepository honeyBatchRepository;
    private final BeekeeperProfileRepository beekeeperProfileRepository;
    private final HiveRepository hiveRepository;
    private final LabTestRepository labTestRepository;
    private final BlockchainRecordRepository blockchainRecordRepository;
    private final QrCodeRepository qrCodeRepository;
    private final VerificationHistoryRepository verificationHistoryRepository;
    private final ProductRepository productRepository;

    public AdminBatchServiceImpl(HoneyBatchRepository honeyBatchRepository,
            BeekeeperProfileRepository beekeeperProfileRepository,
            HiveRepository hiveRepository,
            LabTestRepository labTestRepository,
            BlockchainRecordRepository blockchainRecordRepository,
            QrCodeRepository qrCodeRepository,
            VerificationHistoryRepository verificationHistoryRepository,
            ProductRepository productRepository) {
        this.honeyBatchRepository = honeyBatchRepository;
        this.beekeeperProfileRepository = beekeeperProfileRepository;
        this.hiveRepository = hiveRepository;
        this.labTestRepository = labTestRepository;
        this.blockchainRecordRepository = blockchainRecordRepository;
        this.qrCodeRepository = qrCodeRepository;
        this.verificationHistoryRepository = verificationHistoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public PageResponse<AdminBatchResponse> getBatches(BatchStatus status, String search, Pageable pageable) {
        String cleanSearch = (search != null && !search.isBlank()) ? search.trim() : null;
        Page<HoneyBatch> page = honeyBatchRepository.findAdminBatches(status, cleanSearch, pageable);
        List<AdminBatchResponse> content = page.getContent().stream().map(this::toBatchResponse).toList();
        return PageResponse.of(page, content);
    }

    @Override
    public AdminBatchDetailsResponse getBatchDetails(String batchId) {
        HoneyBatch batch = honeyBatchRepository.findByBatchId(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Honey batch not found with ID: " + batchId));

        AdminBatchDetailsResponse details = new AdminBatchDetailsResponse();
        details.setId(batch.getId());
        details.setBatchId(batch.getBatchId());
        details.setQuantityKg(batch.getQuantityKg() != null ? batch.getQuantityKg().doubleValue() : null);
        details.setHarvestDate(batch.getHarvestDate());
        details.setStatus(batch.getStatus());
        details.setPhotoUrl(batch.getPhotoUrl());
        details.setCreatedAt(batch.getCreatedAt());

        // Beekeeper Info
        beekeeperProfileRepository.findById(batch.getBeekeeperProfileId()).ifPresent(bp -> {
            details.setBeekeeperId(bp.getId());
            details.setBeekeeperName(bp.getName());
            details.setBeekeeperVillage(bp.getVillage());
            details.setBeekeeperKvicId(bp.getKvicId());
        });

        // Hive Info
        hiveRepository.findById(batch.getHiveId()).ifPresent(h -> {
            details.setHiveId(h.getId());
            details.setHiveCode(h.getHiveCode());
            details.setClusterName(h.getClusterName());
        });

        // Lab Info
        Optional<LabTest> labTestOpt = labTestRepository.findByBatchId(batch.getBatchId());
        if (labTestOpt.isPresent()) {
            LabTest lab = labTestOpt.get();
            details.setLabTested(true);
            details.setPurityScore(lab.getPurityScore());
            details.setLabResult(lab.getResult());
            details.setLabName("NABL Certified State Honey Lab");
            details.setTestDate(lab.getTestedAt() != null ? lab.getTestedAt() : lab.getCreatedAt());
        } else {
            details.setLabTested(false);
        }

        // Blockchain Info
        Optional<BlockchainRecord> bcRecordOpt = blockchainRecordRepository
                .findByBatchIdAndRecordType(batch.getBatchId(), BlockchainRecordType.BATCH_CREATED);
        if (bcRecordOpt.isPresent()) {
            BlockchainRecord bc = bcRecordOpt.get();
            details.setBlockchainRecorded(true);
            details.setDataHash(bc.getDataHash());
            details.setTransactionHash(bc.getTransactionHash());
            details.setBlockNumber(bc.getBlockNumber());
        } else {
            details.setBlockchainRecorded(false);
        }

        // QR Info
        qrCodeRepository.findByBatchId(batch.getBatchId()).ifPresentOrElse(qr -> {
            details.setQrGenerated(true);
            details.setQrCodeUrl(qr.getQrImageUrl());
            details.setPublicVerificationUrl(qr.getQrValue());
        }, () -> details.setQrGenerated(false));

        // Verification Scan Activity
        long totalScans = verificationHistoryRepository.countByBatchId(batch.getBatchId());
        details.setTotalScans(totalScans);

        Optional<VerificationHistory> latestScan = verificationHistoryRepository
                .findTop1ByBatchIdOrderByScannedAtDesc(batch.getBatchId());
        if (latestScan.isPresent()) {
            details.setRiskLevel(latestScan.get().getRiskLevel());
            details.setLastScannedAt(latestScan.get().getScannedAt());
        } else {
            details.setRiskLevel(VerificationRiskLevel.NORMAL);
        }

        // Marketplace Product Info
        productRepository.findByBatchBatchId(batch.getBatchId()).ifPresentOrElse(p -> {
            details.setProductListed(true);
            details.setProductId(p.getId());
            details.setPricePerKg(p.getPricePerKg() != null ? p.getPricePerKg().doubleValue() : null);
        }, () -> details.setProductListed(false));

        return details;
    }

    private AdminBatchResponse toBatchResponse(HoneyBatch batch) {
        AdminBatchResponse res = new AdminBatchResponse();
        res.setId(batch.getId());
        res.setBatchId(batch.getBatchId());
        res.setBeekeeperProfileId(batch.getBeekeeperProfileId());
        res.setHiveId(batch.getHiveId());
        res.setHarvestDate(batch.getHarvestDate());
        res.setQuantityKg(batch.getQuantityKg() != null ? batch.getQuantityKg().doubleValue() : null);
        res.setStatus(batch.getStatus());
        res.setCreatedAt(batch.getCreatedAt());

        beekeeperProfileRepository.findById(batch.getBeekeeperProfileId()).ifPresent(bp -> {
            res.setBeekeeperName(bp.getName());
            res.setVillage(bp.getVillage());
        });

        hiveRepository.findById(batch.getHiveId()).ifPresent(h -> res.setHiveCode(h.getHiveCode()));

        labTestRepository.findByBatchId(batch.getBatchId()).ifPresent(l -> res.setPurityScore(l.getPurityScore()));

        blockchainRecordRepository.findByBatchIdAndRecordType(batch.getBatchId(), BlockchainRecordType.BATCH_CREATED)
                .ifPresent(bc -> res.setBlockchainTxHash(bc.getTransactionHash()));

        res.setQrGenerated(qrCodeRepository.existsByBatchId(batch.getBatchId()));

        long scans = verificationHistoryRepository.countByBatchId(batch.getBatchId());
        res.setVerificationCount(scans);

        verificationHistoryRepository.findTop1ByBatchIdOrderByScannedAtDesc(batch.getBatchId())
                .ifPresentOrElse(
                        v -> res.setRiskLevel(v.getRiskLevel()),
                        () -> res.setRiskLevel(VerificationRiskLevel.NORMAL));

        return res;
    }
}
