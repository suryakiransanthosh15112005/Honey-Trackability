package com.honeychain.cart.entity;

import com.honeychain.common.entity.BaseEntity;
import com.honeychain.marketplace.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;

/**
 * A single line item in a customer's cart.
 * unitPriceSnapshot is always set from the current product.pricePerKg at the
 * time the item is added or updated — the client-submitted price is NEVER trusted.
 *
 * Unique constraint prevents duplicate product entries in the same cart.
 */
@Entity
@Table(name = "cart_items",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_cart_items_cart_product", columnNames = {"cart_id", "product_id"})
        },
        indexes = {
                @Index(name = "idx_cart_items_cart_id", columnList = "cart_id"),
                @Index(name = "idx_cart_items_product_id", columnList = "product_id")
        }
)
public class CartItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /** Quantity in kilograms — must be > 0. */
    @Column(name = "quantity_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityKg;

    /**
     * Price captured from product.pricePerKg at add/update time.
     * Used to display consistent subtotals during the cart session.
     * Final order price is always recalculated server-side at checkout.
     */
    @Column(name = "unit_price_snapshot", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPriceSnapshot;

    public CartItem() {}

    public CartItem(Cart cart, Product product, BigDecimal quantityKg, BigDecimal unitPriceSnapshot) {
        this.cart = cart;
        this.product = product;
        this.quantityKg = quantityKg;
        this.unitPriceSnapshot = unitPriceSnapshot;
    }

    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public BigDecimal getQuantityKg() { return quantityKg; }
    public void setQuantityKg(BigDecimal quantityKg) { this.quantityKg = quantityKg; }

    public BigDecimal getUnitPriceSnapshot() { return unitPriceSnapshot; }
    public void setUnitPriceSnapshot(BigDecimal unitPriceSnapshot) { this.unitPriceSnapshot = unitPriceSnapshot; }
}
