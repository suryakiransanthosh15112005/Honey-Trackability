package com.honeychain.beekeeper.repository;

import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.beekeeper.entity.BeekeeperVerificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BeekeeperProfileRepository extends JpaRepository<BeekeeperProfile, Long> {

    Optional<BeekeeperProfile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    boolean existsByKvicId(String kvicId);

    Optional<BeekeeperProfile> findByKvicId(String kvicId);

    @Query("SELECT b FROM BeekeeperProfile b, User u WHERE b.userId = u.id AND u.phoneNumber = :phoneNumber")
    Optional<BeekeeperProfile> findByUserPhoneNumber(@Param("phoneNumber") String phoneNumber);

    long countByVerificationStatus(BeekeeperVerificationStatus verificationStatus);

    @Query("SELECT b FROM BeekeeperProfile b WHERE " +
            "(:status IS NULL OR b.verificationStatus = :status) AND " +
            "(:search IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(b.kvicId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(b.village) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<BeekeeperProfile> findAdminBeekeepers(
            @Param("status") BeekeeperVerificationStatus status,
            @Param("search") String search,
            Pageable pageable);
}
