package com.honeychain;

import com.honeychain.blockchain.exception.BlockchainException;
import com.honeychain.blockchain.util.HashUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashUtilTest {

    @Test
    @DisplayName("1. SHA-256 is deterministic: same input produces same hash")
    void testDeterministicSha256() {
        String input = "batchId=HC-2026-TEST001|harvestDate=2026-09-03|hiveId=1|quantityKg=8.50";
        String hash1 = HashUtil.generateSha256(input);
        String hash2 = HashUtil.generateSha256(input);

        assertNotNull(hash1);
        assertEquals(64, hash1.length());
        assertEquals(hash1, hash2);
        // Verify lowercase hexadecimal
        assertTrue(hash1.matches("^[a-f0-9]{64}$"));
    }

    @Test
    @DisplayName("2. Different inputs produce different hashes")
    void testDifferentInputProducesDifferentHash() {
        String input1 = "batchId=HC-2026-TEST001|quantityKg=8.50";
        String input2 = "batchId=HC-2026-TEST001|quantityKg=8.51";

        String hash1 = HashUtil.generateSha256(input1);
        String hash2 = HashUtil.generateSha256(input2);

        assertNotEquals(hash1, hash2);
    }

    @Test
    @DisplayName("3. Null input throws BlockchainException")
    void testNullInputThrowsException() {
        assertThrows(BlockchainException.class, () -> HashUtil.generateSha256(null));
    }
}
