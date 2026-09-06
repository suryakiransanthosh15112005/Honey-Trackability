package com.honeychain.order.entity;

/**
 * Order lifecycle status.
 *
 * Valid transitions (enforced in OrderServiceImpl):
 *   CONFIRMED → PACKED      (beekeeper packs the order)
 *   CONFIRMED → CANCELLED   (cancellation before packing)
 *   PACKED    → SHIPPED     (beekeeper ships)
 *   SHIPPED   → DELIVERED   (beekeeper marks delivered — last-mile for artisan honey)
 *
 * Invalid transitions:
 *   DELIVERED → *           (terminal state)
 *   CANCELLED → *           (terminal state)
 *   SHIPPED   → CONFIRMED   (no reversal)
 */
public enum OrderStatus {
    CONFIRMED,
    PACKED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
