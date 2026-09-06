package com.honeychain.cart.repository;

import com.honeychain.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByCustomerId(Long customerId);

    /** Convenience query to find a cart by the customer's phone number via JOIN to users. */
    @Query("SELECT c FROM Cart c JOIN User u ON c.customerId = u.id WHERE u.phoneNumber = :phoneNumber")
    Optional<Cart> findByCustomerPhoneNumber(String phoneNumber);

    boolean existsByCustomerId(Long customerId);
}
