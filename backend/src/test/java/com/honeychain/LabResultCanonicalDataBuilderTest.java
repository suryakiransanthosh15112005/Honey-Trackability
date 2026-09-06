package com.honeychain;

import com.honeychain.blockchain.exception.BlockchainException;
import com.honeychain.blockchain.util.LabResultCanonicalDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LabResultCanonicalDataBuilderTest {

    @Test
    @DisplayName("1. Canonical lab string is deterministic and follows fixed format")
    void testCanonicalLabStringFormat() {
        LocalDateTime testedAt = LocalDateTime.of(2026, 9, 3, 20, 30, 0);
        String canonical = LabResultCanonicalDataBuilder.buildCanonicalString(
                "HC-2026-AB12CD34",
                98,
                "PURE",
                testedAt
        );

        assertEquals("batchId=HC-2026-AB12CD34|purityScore=98|result=PURE|testedAt=2026-09-03T20:30:00", canonical);
    }

    @Test
    @DisplayName("2. Case insensitivity on result: pure/PURE produces normalized uppercase")
    void testResultNormalization() {
        LocalDateTime testedAt = LocalDateTime.of(2026, 9, 3, 20, 30, 0);
        String c1 = LabResultCanonicalDataBuilder.buildCanonicalString("HC-01", 95, "pure", testedAt);
        String c2 = LabResultCanonicalDataBuilder.buildCanonicalString("HC-01", 95, "PURE", testedAt);

        assertEquals(c1, c2);
        assertTrue(c1.contains("result=PURE"));
    }

    @Test
    @DisplayName("3. Missing parameters throw BlockchainException")
    void testMissingParameters() {
        LocalDateTime now = LocalDateTime.now();
        assertThrows(BlockchainException.class, () ->
                LabResultCanonicalDataBuilder.buildCanonicalString(null, 90, "PURE", now));
        assertThrows(BlockchainException.class, () ->
                LabResultCanonicalDataBuilder.buildCanonicalString("HC-01", null, "PURE", now));
        assertThrows(BlockchainException.class, () ->
                LabResultCanonicalDataBuilder.buildCanonicalString("HC-01", 90, null, now));
        assertThrows(BlockchainException.class, () ->
                LabResultCanonicalDataBuilder.buildCanonicalString("HC-01", 90, "PURE", null));
    }
}
