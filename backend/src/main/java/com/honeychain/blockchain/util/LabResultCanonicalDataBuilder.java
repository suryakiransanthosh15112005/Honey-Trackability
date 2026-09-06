package com.honeychain.blockchain.util;

import com.honeychain.blockchain.exception.BlockchainException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class LabResultCanonicalDataBuilder {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private LabResultCanonicalDataBuilder() {
    }

    /**
     * Builds a deterministic canonical string representation of a lab test result for blockchain recording.
     * Format: batchId={batchId}|purityScore={score}|result={result}|testedAt={yyyy-MM-dd'T'HH:mm:ss}
     */
    public static String buildCanonicalString(String batchId, Integer purityScore, String result, LocalDateTime testedAt) {
        if (batchId == null || batchId.isBlank()) {
            throw new BlockchainException("Cannot build canonical lab result data: batchId is required");
        }
        if (purityScore == null) {
            throw new BlockchainException("Cannot build canonical lab result data: purityScore is required");
        }
        if (result == null || result.isBlank()) {
            throw new BlockchainException("Cannot build canonical lab result data: result is required");
        }
        if (testedAt == null) {
            throw new BlockchainException("Cannot build canonical lab result data: testedAt is required");
        }

        String formattedTime = testedAt.format(DATE_TIME_FORMATTER);

        return String.format(
                "batchId=%s|purityScore=%d|result=%s|testedAt=%s",
                batchId.trim(),
                purityScore,
                result.trim().toUpperCase(),
                formattedTime
        );
    }
}
