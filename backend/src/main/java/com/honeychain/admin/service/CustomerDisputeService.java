package com.honeychain.admin.service;

import com.honeychain.admin.dto.CreateDisputeRequest;
import com.honeychain.admin.dto.DisputeResponse;
import com.honeychain.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

public interface CustomerDisputeService {

    DisputeResponse createDispute(String customerPhoneNumber, CreateDisputeRequest request);

    PageResponse<DisputeResponse> getMyDisputes(String customerPhoneNumber, Pageable pageable);

    DisputeResponse getDisputeById(String customerPhoneNumber, Long id);
}
