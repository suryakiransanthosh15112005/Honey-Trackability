package com.honeychain.auth.service;

public interface OtpService {

    /**
     * Generates and dispatches a one-time password for the given phone number.
     *
     * @param phoneNumber recipient phone number
     * @return generated OTP (for internal / logging purposes)
     */
    String generateAndSendOtp(String phoneNumber);

    /**
     * Verifies the provided OTP against the stored active OTP.
     *
     * @param phoneNumber phone number to verify
     * @param otp         OTP provided by the user
     * @return true if valid and not expired, false otherwise
     */
    boolean verifyOtp(String phoneNumber, String otp);
}
