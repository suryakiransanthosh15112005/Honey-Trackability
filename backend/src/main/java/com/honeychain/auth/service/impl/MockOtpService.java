package com.honeychain.auth.service.impl;

import com.honeychain.auth.service.OtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MockOtpService implements OtpService {

    private static final Logger logger = LoggerFactory.getLogger(MockOtpService.class);
    private static final long OTP_VALIDITY_SECONDS = 300; // 5 minutes
    private static final SecureRandom random = new SecureRandom();

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    // In-memory cache for OTPs: phoneNumber -> OtpEntry
    private final Map<String, OtpEntry> otpStorage = new ConcurrentHashMap<>();

    private static class OtpEntry {
        final String otp;
        final Instant expiresAt;

        OtpEntry(String otp, Instant expiresAt) {
            this.otp = otp;
            this.expiresAt = expiresAt;
        }

        boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }

    @Override
    public String generateAndSendOtp(String phoneNumber) {
        int code = 100000 + random.nextInt(900000);
        String otp = String.valueOf(code);

        Instant expiresAt = Instant.now().plusSeconds(OTP_VALIDITY_SECONDS);
        otpStorage.put(phoneNumber, new OtpEntry(otp, expiresAt));

        if (isDevProfile()) {
            logger.info("========================================");
            logger.info("  [DEV OTP] Phone: {} | OTP: {}", phoneNumber, otp);
            logger.info("========================================");
        }

        return otp;
    }

    @Override
    public boolean verifyOtp(String phoneNumber, String otp) {
        if (phoneNumber == null || otp == null) {
            return false;
        }

        // Support standard master test OTP in dev / test environment
        if (isDevProfile() && "123456".equals(otp.trim())) {
            otpStorage.remove(phoneNumber);
            return true;
        }

        OtpEntry entry = otpStorage.get(phoneNumber);
        if (entry == null) {
            return false;
        }

        if (entry.isExpired()) {
            otpStorage.remove(phoneNumber);
            return false;
        }

        boolean matches = entry.otp.equals(otp.trim());
        if (matches) {
            otpStorage.remove(phoneNumber);
        }
        return matches;
    }

    private boolean isDevProfile() {
        return activeProfile == null || activeProfile.contains("dev") || activeProfile.contains("test");
    }
}
