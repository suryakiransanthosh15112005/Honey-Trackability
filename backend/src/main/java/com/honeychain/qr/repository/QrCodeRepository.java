package com.honeychain.qr.repository;

import com.honeychain.qr.entity.QrCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QrCodeRepository extends JpaRepository<QrCode, Long> {

    Optional<QrCode> findByBatchId(String batchId);

    boolean existsByBatchId(String batchId);
}
