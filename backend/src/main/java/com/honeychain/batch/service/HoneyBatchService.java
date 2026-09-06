package com.honeychain.batch.service;

import com.honeychain.batch.dto.HoneyBatchCreateRequest;
import com.honeychain.batch.dto.HoneyBatchResponse;
import com.honeychain.batch.dto.HoneyBatchUpdateRequest;
import com.honeychain.common.dto.PageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface HoneyBatchService {

        /**
         * Creates a honey batch for the given beekeeper.
         * If idempotencyKey is non-null and a batch was already created with it by
         * the same beekeeper, the existing batch is returned without creating a
         * duplicate.
         */
        HoneyBatchResponse createBatch(String phoneNumber, HoneyBatchCreateRequest request,
                        MultipartFile photo, String idempotencyKey);

        PageResponse<HoneyBatchResponse> getMyBatches(String phoneNumber, int page, int size, String sort);

        HoneyBatchResponse getMyBatch(String phoneNumber, String batchId);

        HoneyBatchResponse updateBatch(String phoneNumber, String batchId, HoneyBatchUpdateRequest request,
                        MultipartFile photo);

        HoneyBatchResponse sendForTesting(String phoneNumber, String batchId);

        Map<String, Long> getBatchStats(String phoneNumber);
}
