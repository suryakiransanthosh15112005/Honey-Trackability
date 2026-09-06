package com.honeychain.cart.entity;

import com.honeychain.common.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.ArrayList;
import java.util.List;

/**
 * One active cart per customer (enforced by unique constraint on customer_id).
 * Cart is created on demand when the customer first adds an item.
 */
@Entity
@Table(name = "carts",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_carts_customer_id", columnNames = {"customer_id"})
        },
        indexes = {
                @Index(name = "idx_carts_customer_id", columnList = "customer_id")
        }
)
public class Cart extends BaseEntity {

    /** FK to users.id — stored as Long, loaded via UserRepository. */
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    public Cart() {}

    public Cart(Long customerId) {
        this.customerId = customerId;
    }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    public void clearItems() {
        this.items.clear();
    }
}
