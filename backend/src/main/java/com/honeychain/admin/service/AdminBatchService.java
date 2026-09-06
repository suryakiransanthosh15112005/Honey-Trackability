package com.honeychain.admin.service;

import com.honeychain.admin.dto.AdminBatchDetailsResponse;
import com.honeychain.admin.dto.AdminBatchResponse;
import com.honeychain.batch.entity.BatchStatus;
import com.honeychain.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

public interface AdminBatchService {

    PageResponse<AdminBatchResponse> getBatches(BatchStatus status, String search, Pageable pageable);

    AdminBatchDetailsResponse getBatchDetails(String batchId);
}
