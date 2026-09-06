package com.honeychain.payment.service.impl;

import com.honeychain.payment.dto.PaymentRequest;
import com.honeychain.payment.dto.PaymentResult;
import com.honeychain.payment.service.PaymentService;
import com.honeychain.payment.service.PaymentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * MOCK payment implementation for Phase 13 MVP.
 *
 * IMPORTANT: This implementation does NOT process real money.
 * It is a placeholder that will be replaced by a real Razorpay/UPI
 * implementation in a future phase.
 *
 * Behavior:
 *  - paymentMode = "fail"  →  returns FAILED  (for dev/test)
 *  - anything else         →  returns SUCCESS
 *
 * No real credentials, card numbers, or bank details are used.
 */
@Service
public class MockPaymentService implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(MockPaymentService.class);

    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        logger.info("[MOCK-PAYMENT] Processing demo payment for order={}, amount=INR {}, customerId={}",
                request.getOrderReference(), request.getAmount(), request.getCustomerId());

        // Dev/test hook: pass paymentMode=fail to simulate payment failure
        if ("fail".equalsIgnoreCase(request.getPaymentMode())) {
            logger.warn("[MOCK-PAYMENT] Simulated payment FAILURE requested via paymentMode=fail");
            return new PaymentResult(null, PaymentStatus.FAILED,
                    "Demo payment simulation: payment declined (test mode)");
        }

        // Generate a mock payment ID based on timestamp + order reference
        String mockPaymentId = "MOCK-PAY-" + Instant.now().toEpochMilli();

        logger.info("[MOCK-PAYMENT] Demo payment SUCCESS — paymentId={}", mockPaymentId);
        return new PaymentResult(mockPaymentId, PaymentStatus.SUCCESS,
                "Demo payment processed successfully (no real money charged)");
    }
}
