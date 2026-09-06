package com.honeychain.blockchain.repository;

import com.honeychain.blockchain.entity.BlockchainRecord;
import com.honeychain.blockchain.entity.BlockchainRecordType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockchainRecordRepository extends JpaRepository<BlockchainRecord, Long> {

    List<BlockchainRecord> findAllByBatchId(String batchId);

    Optional<BlockchainRecord> findByBatchIdAndRecordType(String batchId, BlockchainRecordType recordType);

    boolean existsByBatchIdAndRecordType(String batchId, BlockchainRecordType recordType);

    Optional<BlockchainRecord> findByTransactionHash(String transactionHash);
}
