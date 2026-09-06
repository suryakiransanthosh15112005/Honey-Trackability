package com.honeychain.payment.service;

/**
 * Represents the possible states of a payment transaction.
 * REFUNDED is reserved for future phases.
 */
public enum PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED,
    REFUNDED
}
