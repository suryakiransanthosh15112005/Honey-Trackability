package com.honeychain.batch.repository;

import com.honeychain.batch.entity.BatchStatus;
import com.honeychain.batch.entity.HoneyBatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HoneyBatchRepository extends JpaRepository<HoneyBatch, Long> {

    Optional<HoneyBatch> findByBatchId(String batchId);

    Optional<HoneyBatch> findByBatchIdAndBeekeeperProfileId(String batchId, Long beekeeperProfileId);

    Page<HoneyBatch> findAllByBeekeeperProfileId(Long beekeeperProfileId, Pageable pageable);

    List<HoneyBatch> findAllByBeekeeperProfileId(Long beekeeperProfileId);

    boolean existsByBatchId(String batchId);

    long countByBeekeeperProfileId(Long beekeeperProfileId);

    List<HoneyBatch> findAllByHiveIdOrderByHarvestDateDesc(Long hiveId);

    long countByStatus(BatchStatus status);

    @Query("SELECT COALESCE(SUM(b.quantityKg), 0.0) FROM HoneyBatch b")
    Double sumTotalHoneyProducedKg();

    @Query("SELECT b FROM HoneyBatch b WHERE " +
            "(:status IS NULL OR b.status = :status) AND " +
            "(:search IS NULL OR LOWER(b.batchId) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<HoneyBatch> findAdminBatches(
            @Param("status") BatchStatus status,
            @Param("search") String search,
            Pageable pageable);
}
