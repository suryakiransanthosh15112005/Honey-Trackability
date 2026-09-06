package com.honeychain.order.dto;

import com.honeychain.order.entity.FulfillmentType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class CheckoutRequest {

    @NotNull(message = "Fulfillment type is required")
    private FulfillmentType fulfillmentType;

    @Valid
    private DeliveryAddressDto deliveryAddress;

    private String paymentMode; // "mock" or "fail" (for testing)

    public CheckoutRequest() {}

    public CheckoutRequest(FulfillmentType fulfillmentType, DeliveryAddressDto deliveryAddress, String paymentMode) {
        this.fulfillmentType = fulfillmentType;
        this.deliveryAddress = deliveryAddress;
        this.paymentMode = paymentMode;
    }

    public FulfillmentType getFulfillmentType() {
        return fulfillmentType;
    }

    public void setFulfillmentType(FulfillmentType fulfillmentType) {
        this.fulfillmentType = fulfillmentType;
    }

    public DeliveryAddressDto getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(DeliveryAddressDto deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }
}
