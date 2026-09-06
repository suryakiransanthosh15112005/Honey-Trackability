package com.honeychain.order.entity;

/**
 * Defines how the customer wants to receive the honey.
 */
public enum FulfillmentType {
    /** Standard delivery — requires a delivery address. */
    DELIVERY,
    /** Customer picks up directly from the beekeeper. */
    LOCAL_PICKUP
}
