package com.honeychain.order.repository;

import com.honeychain.order.entity.Order;
import com.honeychain.order.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    Optional<Order> findByOrderNumberAndCustomerId(String orderNumber, Long customerId);

    Page<Order> findAllByCustomerIdOrderByCreatedAtDesc(Long customerId, Pageable pageable);

    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderItems oi JOIN Product p ON oi.productId = p.id " +
            "WHERE p.beekeeperProfile.id = :beekeeperProfileId ORDER BY o.createdAt DESC")
    Page<Order> findOrdersByBeekeeperProfileId(@Param("beekeeperProfileId") Long beekeeperProfileId, Pageable pageable);

    @Query("SELECT COUNT(o) FROM Order o")
    long countTotalOrders();

    long countByOrderStatus(OrderStatus orderStatus);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0.0) FROM Order o WHERE o.orderStatus != com.honeychain.order.entity.OrderStatus.CANCELLED")
    Double sumTotalRevenue();
}
