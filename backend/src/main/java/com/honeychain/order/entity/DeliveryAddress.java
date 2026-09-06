package com.honeychain.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Embeddable delivery address stored inline in the orders table.
 * Only populated when fulfillmentType = DELIVERY.
 */
@Embeddable
public class DeliveryAddress {

    @Column(name = "delivery_name", length = 100)
    private String name;

    @Column(name = "delivery_line1", length = 255)
    private String line1;

    @Column(name = "delivery_line2", length = 255)
    private String line2;

    @Column(name = "delivery_city", length = 100)
    private String city;

    @Column(name = "delivery_state", length = 100)
    private String state;

    @Column(name = "delivery_postal_code", length = 20)
    private String postalCode;

    public DeliveryAddress() {}

    public DeliveryAddress(String name, String line1, String line2,
                           String city, String state, String postalCode) {
        this.name = name;
        this.line1 = line1;
        this.line2 = line2;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLine1() { return line1; }
    public void setLine1(String line1) { this.line1 = line1; }

    public String getLine2() { return line2; }
    public void setLine2(String line2) { this.line2 = line2; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
}
