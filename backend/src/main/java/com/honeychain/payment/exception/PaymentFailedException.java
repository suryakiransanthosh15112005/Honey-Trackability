package com.honeychain.payment.exception;

/**
 * Thrown when the PaymentService returns a FAILED result during checkout.
 *
 * This is a RuntimeException so it triggers @Transactional rollback,
 * ensuring cart and inventory are NOT modified on payment failure.
 */
public class PaymentFailedException extends RuntimeException {

    public PaymentFailedException(String message) {
        super(message);
    }

    public PaymentFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
