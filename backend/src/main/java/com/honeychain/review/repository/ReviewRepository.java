package com.honeychain.review.repository;

import com.honeychain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByProductIdOrderByCreatedAtDesc(Long productId, Pageable pageable);

    Page<Review> findByCustomerIdOrderByCreatedAtDesc(Long customerId, Pageable pageable);

    Optional<Review> findByIdAndCustomerId(Long id, Long customerId);

    boolean existsByCustomerIdAndOrderItemId(Long customerId, Long orderItemId);

    Optional<Review> findByCustomerIdAndOrderItemId(Long customerId, Long orderItemId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double getAverageRatingByProductId(@Param("productId") Long productId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId")
    long countByProductId(@Param("productId") Long productId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.beekeeper.id = :beekeeperId")
    Double getAverageRatingByBeekeeperId(@Param("beekeeperId") Long beekeeperId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.beekeeper.id = :beekeeperId")
    long countByBeekeeperId(@Param("beekeeperId") Long beekeeperId);
}
