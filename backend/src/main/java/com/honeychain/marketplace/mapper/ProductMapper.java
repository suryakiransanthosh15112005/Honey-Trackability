package com.honeychain.marketplace.mapper;

import com.honeychain.batch.entity.HoneyBatch;
import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.lab.entity.LabTest;
import com.honeychain.lab.repository.LabTestRepository;
import com.honeychain.marketplace.dto.ProductResponse;
import com.honeychain.marketplace.entity.Product;
import com.honeychain.verification.dto.PublicVerificationResponse;
import com.honeychain.verification.service.PublicVerificationService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProductMapper {

    private final LabTestRepository labTestRepository;
    private final PublicVerificationService verificationService;

    public ProductMapper(LabTestRepository labTestRepository, PublicVerificationService verificationService) {
        this.labTestRepository = labTestRepository;
        this.verificationService = verificationService;
    }

    public ProductResponse toResponse(Product entity) {
        if (entity == null) {
            return null;
        }

        HoneyBatch batch = entity.getBatch();
        BeekeeperProfile profile = entity.getBeekeeperProfile();

        // Retrieve lab test purity score
        Integer purityScore = null;
        if (batch != null) {
            Optional<LabTest> labTestOpt = labTestRepository.findByBatchId(batch.getBatchId());
            if (labTestOpt.isPresent()) {
                purityScore = labTestOpt.get().getPurityScore();
            }
        }

        // Retrieve blockchain verification summary
        boolean verified = false;
        if (batch != null) {
            try {
                PublicVerificationResponse verification = verificationService.verifyBatch(batch.getBatchId());
                verified = verification.isVerified();
                if (purityScore == null && verification.getPurity() != null) {
                    purityScore = verification.getPurity().getScore();
                }
            } catch (Exception ignored) {
                // Default verified boolean to false if batch not yet verified
            }
        }

        ProductResponse.BeekeeperSummary beekeeperSummary = null;
        if (profile != null) {
            beekeeperSummary = new ProductResponse.BeekeeperSummary(
                    profile.getName(),
                    profile.getVillage(),
                    profile.getPhotoUrl(),
                    profile.getKvicId());
        }

        return new ProductResponse(
                entity.getId(),
                batch != null ? batch.getBatchId() : null,
                batch != null ? batch.getHarvestDate() : null,
                batch != null ? batch.getStatus().name() : null,
                entity.getProductName(),
                entity.getFlowerSource(),
                entity.getRegion(),
                entity.getPricePerKg(),
                entity.getAvailableQuantityKg(),
                entity.getDescription(),
                entity.getImageUrl(),
                entity.getRating(),
                entity.getReviewCount(),
                verified,
                purityScore,
                entity.getIsActive(),
                beekeeperSummary);
    }
}
