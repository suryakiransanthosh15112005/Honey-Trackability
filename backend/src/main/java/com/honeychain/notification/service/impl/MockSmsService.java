package com.honeychain.notification.service.impl;

import com.honeychain.notification.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MockSmsService implements SmsService {

    private static final Logger logger = LoggerFactory.getLogger(MockSmsService.class);

    @Value("${notification.sms.enabled:true}")
    private boolean smsEnabled;

    @Override
    public void sendSms(String phoneNumber, String message) {
        if (!smsEnabled) {
            logger.debug("[SMS DISABLED] Skipping SMS send to {}", maskPhoneNumber(phoneNumber));
            return;
        }

        String masked = maskPhoneNumber(phoneNumber);
        logger.info("[MOCK SMS] To: {} | Message: {}", masked, message);
    }

    /**
     * Mask phone number for logger privacy.
     * Example: "+919876543210" -> "+91******3210" or "9876543210" -> "******3210"
     */
    private String maskPhoneNumber(String phone) {
        if (phone == null || phone.isBlank())
            return "N/A";
        int len = phone.length();
        if (len <= 4)
            return "****";
        String lastFour = phone.substring(len - 4);
        String prefix = len > 10 ? phone.substring(0, len - 8) : "";
        return prefix + "******" + lastFour;
    }
}
