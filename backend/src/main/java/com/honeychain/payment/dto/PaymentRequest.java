package com.honeychain.payment.dto;

import java.math.BigDecimal;

/**
 * Encapsulates the data needed to initiate a payment.
 * This DTO is passed from OrderService to PaymentService — no HTTP exposure.
 */
public class PaymentRequest {

    private Long customerId;
    private BigDecimal amount;
    private String currency;
    private String paymentMode;   // e.g. "mock", "razorpay" (future)
    private String orderReference; // order number for idempotency

    public PaymentRequest() {}

    public PaymentRequest(Long customerId, BigDecimal amount, String currency,
                          String paymentMode, String orderReference) {
        this.customerId = customerId;
        this.amount = amount;
        this.currency = currency;
        this.paymentMode = paymentMode;
        this.orderReference = orderReference;
    }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

    public String getOrderReference() { return orderReference; }
    public void setOrderReference(String orderReference) { this.orderReference = orderReference; }
}
