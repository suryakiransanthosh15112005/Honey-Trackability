package com.honeychain.blockchain.util;

import com.honeychain.batch.entity.HoneyBatch;
import com.honeychain.blockchain.exception.BlockchainException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class BatchCanonicalDataBuilder {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private BatchCanonicalDataBuilder() {
    }

    /**
     * Builds a deterministic canonical string representation of a batch for hashing.
     * Format: batchId={batchId}|harvestDate={yyyy-MM-dd}|hiveId={hiveId}|quantityKg={0.00}
     */
    public static String buildCanonicalString(String batchId, LocalDate harvestDate, Long hiveId, BigDecimal quantityKg) {
        if (batchId == null || batchId.isBlank()) {
            throw new BlockchainException("Cannot build canonical batch data: batchId is required");
        }
        if (harvestDate == null) {
            throw new BlockchainException("Cannot build canonical batch data: harvestDate is required");
        }
        if (hiveId == null) {
            throw new BlockchainException("Cannot build canonical batch data: hiveId is required");
        }
        if (quantityKg == null) {
            throw new BlockchainException("Cannot build canonical batch data: quantityKg is required");
        }

        // Normalize decimal representation to exactly 2 decimal places (e.g. 8.5 -> 8.50)
        String normalizedQuantity = quantityKg.setScale(2, RoundingMode.HALF_UP).toPlainString();
        String formattedDate = harvestDate.format(DATE_FORMATTER);

        return String.format(
                "batchId=%s|harvestDate=%s|hiveId=%d|quantityKg=%s",
                batchId.trim(),
                formattedDate,
                hiveId,
                normalizedQuantity
        );
    }

    public static String buildCanonicalString(HoneyBatch batch) {
        if (batch == null) {
            throw new BlockchainException("Cannot build canonical data from null batch");
        }
        return buildCanonicalString(
                batch.getBatchId(),
                batch.getHarvestDate(),
                batch.getHiveId(),
                batch.getQuantityKg()
        );
    }
}
