package com.honeychain.payment.service;

import com.honeychain.payment.dto.PaymentRequest;
import com.honeychain.payment.dto.PaymentResult;

/**
 * Payment abstraction layer.
 * Implementations can be swapped independently:
 *  - MockPaymentService (Phase 13 MVP)
 *  - RazorpayPaymentService (Phase N — real gateway)
 *  - UpiPaymentService (Phase N)
 *
 * OrderService depends on this interface, never on a concrete implementation.
 */
public interface PaymentService {

    /**
     * Process a payment for the given request.
     * @param request  details of the payment
     * @return         result containing paymentId and status
     */
    PaymentResult processPayment(PaymentRequest request);
}
