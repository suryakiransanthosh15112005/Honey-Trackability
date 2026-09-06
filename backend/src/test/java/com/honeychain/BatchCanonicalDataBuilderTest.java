package com.honeychain;

import com.honeychain.blockchain.exception.BlockchainException;
import com.honeychain.blockchain.util.BatchCanonicalDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BatchCanonicalDataBuilderTest {

        @Test
        @DisplayName("1. Canonical string follows fixed field order and format")
        void testCanonicalStringFormat() {
                String result = BatchCanonicalDataBuilder.buildCanonicalString(
                                "HC-2026-AB12CD34",
                                LocalDate.of(2026, 9, 3),
                                1L,
                                new BigDecimal("8.50"));

                assertEquals("batchId=HC-2026-AB12CD34|harvestDate=2026-09-03|hiveId=1|quantityKg=8.50", result);
        }

        @Test
        @DisplayName("2. Decimal normalization: 8.5 and 8.50 produce identical canonical representations")
        void testDecimalNormalization() {
                String result1 = BatchCanonicalDataBuilder.buildCanonicalString(
                                "HC-2026-AB12CD34",
                                LocalDate.of(2026, 9, 3),
                                1L,
                                new BigDecimal("8.5"));

                String result2 = BatchCanonicalDataBuilder.buildCanonicalString(
                                "HC-2026-AB12CD34",
                                LocalDate.of(2026, 9, 3),
                                1L,
                                new BigDecimal("8.50"));

                assertEquals(result1, result2);
                assertTrue(result1.contains("quantityKg=8.50"));
        }

        @Test
        @DisplayName("3. Different batch attributes produce different canonical strings")
        void testDifferentAttributes() {
                String s1 = BatchCanonicalDataBuilder.buildCanonicalString(
                                "HC-2026-0001", LocalDate.of(2026, 9, 1), 1L, new BigDecimal("10.00"));
                String s2 = BatchCanonicalDataBuilder.buildCanonicalString(
                                "HC-2026-0002", LocalDate.of(2026, 9, 1), 1L, new BigDecimal("10.00"));
                String s3 = BatchCanonicalDataBuilder.buildCanonicalString(
                                "HC-2026-0001", LocalDate.of(2026, 9, 2), 1L, new BigDecimal("10.00"));
                String s4 = BatchCanonicalDataBuilder.buildCanonicalString(
                                "HC-2026-0001", LocalDate.of(2026, 9, 1), 2L, new BigDecimal("10.00"));
                String s5 = BatchCanonicalDataBuilder.buildCanonicalString(
                                "HC-2026-0001", LocalDate.of(2026, 9, 1), 1L, new BigDecimal("10.01"));

                assertNotEquals(s1, s2);
                assertNotEquals(s1, s3);
                assertNotEquals(s1, s4);
                assertNotEquals(s1, s5);
        }

        @Test
        @DisplayName("4. Missing required field throws BlockchainException")
        void testMissingFieldValidation() {
                assertThrows(BlockchainException.class, () -> BatchCanonicalDataBuilder.buildCanonicalString(null,
                                LocalDate.now(), 1L, new BigDecimal("5.00")));
                assertThrows(BlockchainException.class, () -> BatchCanonicalDataBuilder.buildCanonicalString("HC-01",
                                null, 1L, new BigDecimal("5.00")));
                assertThrows(BlockchainException.class, () -> BatchCanonicalDataBuilder.buildCanonicalString("HC-01",
                                LocalDate.now(), null, new BigDecimal("5.00")));
                assertThrows(BlockchainException.class, () -> BatchCanonicalDataBuilder.buildCanonicalString("HC-01",
                                LocalDate.now(), 1L, null));
        }
}
