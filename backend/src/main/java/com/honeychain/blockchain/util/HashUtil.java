package com.honeychain.blockchain.util;

import com.honeychain.blockchain.exception.BlockchainException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class HashUtil {

    private HashUtil() {
    }

    public static String generateSha256(String data) {
        if (data == null) {
            throw new BlockchainException("Cannot compute SHA-256 hash of null data");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(hashBytes.length * 2);

            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new BlockchainException("SHA-256 algorithm not available in JVM: " + e.getMessage(), e);
        }
    }
}
