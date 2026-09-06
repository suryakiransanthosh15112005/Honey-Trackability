package com.honeychain.blockchain.mapper;

import com.honeychain.blockchain.dto.BlockchainRecordResponse;
import com.honeychain.blockchain.entity.BlockchainRecord;
import org.springframework.stereotype.Component;

@Component
public class BlockchainRecordMapper {

    public BlockchainRecordResponse toResponse(BlockchainRecord record) {
        if (record == null) {
            return new BlockchainRecordResponse(false, null, null, null, null, null, null, null);
        }
        return new BlockchainRecordResponse(
                true,
                record.getBatchId(),
                record.getDataHash(),
                record.getTransactionHash(),
                record.getBlockNumber(),
                record.getNetwork(),
                record.getRecordType(),
                record.getRecordedAt()
        );
    }
}
