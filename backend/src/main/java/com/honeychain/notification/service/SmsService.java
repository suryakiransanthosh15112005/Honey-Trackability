package com.honeychain.notification.service;

public interface SmsService {

    /**
     * Send an SMS message to the specified phone number.
     * Implementation should handle masking numbers in logs to preserve privacy.
     *
     * @param phoneNumber Destination phone number in E.164 or local format
     * @param message     Text content of the SMS
     */
    void sendSms(String phoneNumber, String message);
}
