package com.honeychain.admin.service;

import com.honeychain.admin.dto.DisputeResponse;
import com.honeychain.admin.dto.DisputeStatusUpdateRequest;
import com.honeychain.admin.entity.DisputeStatus;
import com.honeychain.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

public interface AdminDisputeService {

    PageResponse<DisputeResponse> getDisputes(DisputeStatus status, Pageable pageable);

    DisputeResponse getDisputeDetails(Long id);

    DisputeResponse updateDisputeStatus(Long id, DisputeStatusUpdateRequest request);
}
