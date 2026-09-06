package com.honeychain.hive.repository;

import com.honeychain.hive.entity.Hive;
import com.honeychain.hive.entity.HiveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HiveRepository extends JpaRepository<Hive, Long> {

        List<Hive> findAllByBeekeeperProfileId(Long beekeeperProfileId);

        Optional<Hive> findByIdAndBeekeeperProfileId(Long id, Long beekeeperProfileId);

        boolean existsByHiveCode(String hiveCode);

        long countByBeekeeperProfileId(Long beekeeperProfileId);

        long countByStatus(HiveStatus status);

        @Query("SELECT h FROM Hive h WHERE " +
                        "(:status IS NULL OR h.status = :status) AND " +
                        "(:search IS NULL OR LOWER(h.hiveCode) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(h.clusterName) LIKE LOWER(CONCAT('%', :search, '%')))")
        Page<Hive> findAdminHives(
                        @Param("status") HiveStatus status,
                        @Param("search") String search,
                        Pageable pageable);
}
