package com.honeychain.review.entity;

import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.common.entity.BaseEntity;
import com.honeychain.marketplace.entity.Product;
import com.honeychain.order.entity.Order;
import com.honeychain.order.entity.OrderItem;
import com.honeychain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Represents a customer rating and review for a purchased honey product.
 * A review is permanently linked to an authenticated customer and a specific
 * delivered OrderItem to guarantee authenticity and prevent fake reviews.
 */
@Entity
@Table(name = "reviews",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_reviews_customer_order_item", columnNames = {"customer_id", "order_item_id"})
        },
        indexes = {
                @Index(name = "idx_reviews_product_id", columnList = "product_id"),
                @Index(name = "idx_reviews_beekeeper_id", columnList = "beekeeper_id"),
                @Index(name = "idx_reviews_customer_id", columnList = "customer_id"),
                @Index(name = "idx_reviews_created_at", columnList = "created_at")
        }
)
public class Review extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "beekeeper_id", nullable = false)
    private BeekeeperProfile beekeeper;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    /** Rating value from 1 to 5 stars. */
    @Column(name = "rating", nullable = false)
    private Integer rating;

    /** Optional customer comment / tasting review (max 1000 characters). */
    @Column(name = "comment", length = 1000)
    private String comment;

    public Review() {}

    public Review(User customer, Product product, BeekeeperProfile beekeeper,
                  Order order, OrderItem orderItem, Integer rating, String comment) {
        this.customer = customer;
        this.product = product;
        this.beekeeper = beekeeper;
        this.order = order;
        this.orderItem = orderItem;
        this.rating = rating;
        this.comment = comment;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public BeekeeperProfile getBeekeeper() {
        return beekeeper;
    }

    public void setBeekeeper(BeekeeperProfile beekeeper) {
        this.beekeeper = beekeeper;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
