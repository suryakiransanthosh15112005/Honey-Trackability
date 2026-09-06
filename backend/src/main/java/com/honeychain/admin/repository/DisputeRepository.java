package com.honeychain.admin.repository;

import com.honeychain.admin.entity.Dispute;
import com.honeychain.admin.entity.DisputeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DisputeRepository extends JpaRepository<Dispute, Long> {

    Page<Dispute> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Dispute> findAllByStatusOrderByCreatedAtDesc(DisputeStatus status, Pageable pageable);

    long countByStatus(DisputeStatus status);

    Optional<Dispute> findByBatchId(String batchId);

    Page<Dispute> findByCustomerIdOrderByCreatedAtDesc(Long customerId, Pageable pageable);

    Optional<Dispute> findByIdAndCustomerId(Long id, Long customerId);
}
