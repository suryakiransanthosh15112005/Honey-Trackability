package com.honeychain.verification.util;

import com.honeychain.blockchain.util.HashUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VerificationFingerprintUtil {

    private final String salt;

    public VerificationFingerprintUtil(@Value("${verification.fingerprint-salt:honeychain-secure-salt-2026}") String salt) {
        this.salt = salt;
    }

    /**
     * Generates a privacy-preserving, one-way SHA-256 fingerprint from request context.
     * Raw client identifiers are never persisted or exposed.
     */
    public String generateFingerprint(HttpServletRequest request) {
        if (request == null) {
            return HashUtil.generateSha256("anonymous-client:" + salt);
        }

        String clientIp = extractClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        String acceptLanguage = request.getHeader("Accept-Language");

        String raw = String.format("%s|%s|%s|%s",
                clientIp != null ? clientIp : "unknown-ip",
                userAgent != null ? userAgent : "unknown-agent",
                acceptLanguage != null ? acceptLanguage : "unknown-lang",
                salt
        );

        return HashUtil.generateSha256(raw);
    }

    private String extractClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank()) {
            return xRealIp.trim();
        }
        return request.getRemoteAddr();
    }
}
