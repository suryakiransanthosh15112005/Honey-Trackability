package com.honeychain.payment.dto;

import com.honeychain.payment.service.PaymentStatus;

/**
 * Result returned by PaymentService after processing a payment.
 * The paymentId is stored on the Order for audit purposes.
 */
public class PaymentResult {

    private String paymentId;
    private PaymentStatus status;
    private String message;

    public PaymentResult() {}

    public PaymentResult(String paymentId, PaymentStatus status, String message) {
        this.paymentId = paymentId;
        this.status = status;
        this.message = message;
    }

    public boolean isSuccessful() {
        return PaymentStatus.SUCCESS.equals(this.status);
    }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
