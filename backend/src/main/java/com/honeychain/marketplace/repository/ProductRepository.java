package com.honeychain.marketplace.repository;

import com.honeychain.marketplace.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

        Optional<Product> findByBatchBatchId(String batchId);

        boolean existsByBatchBatchId(String batchId);

        Page<Product> findAllByBeekeeperProfileId(Long beekeeperProfileId, Pageable pageable);

        Optional<Product> findByIdAndBeekeeperProfileId(Long id, Long beekeeperProfileId);

        long countByIsActiveTrue();

        long countByIsActiveFalse();

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("SELECT p FROM Product p WHERE p.id = :id")
        Optional<Product> findByIdForUpdate(@Param("id") Long id);

        @Query("SELECT p FROM Product p " +
                        "WHERE p.isActive = true " +
                        "  AND p.availableQuantityKg > 0 " +
                        "  AND (:search IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :search, '%')) " +
                        "       OR LOWER(p.flowerSource) LIKE LOWER(CONCAT('%', :search, '%')) " +
                        "       OR LOWER(p.region) LIKE LOWER(CONCAT('%', :search, '%'))) " +
                        "  AND (:region IS NULL OR p.region = :region) " +
                        "  AND (:flowerSource IS NULL OR p.flowerSource = :flowerSource) " +
                        "  AND (:minPrice IS NULL OR p.pricePerKg >= :minPrice) " +
                        "  AND (:maxPrice IS NULL OR p.pricePerKg <= :maxPrice) " +
                        "  AND (:minRating IS NULL OR p.rating >= :minRating)")
        Page<Product> findPublicProducts(
                        @Param("search") String search,
                        @Param("region") String region,
                        @Param("flowerSource") String flowerSource,
                        @Param("minPrice") BigDecimal minPrice,
                        @Param("maxPrice") BigDecimal maxPrice,
                        @Param("minRating") BigDecimal minRating,
                        Pageable pageable);
}
