package com.honeychain.admin.service;

import com.honeychain.admin.dto.AdminBeekeeperResponse;
import com.honeychain.admin.dto.AdminBeekeeperStatusUpdateRequest;
import com.honeychain.beekeeper.entity.BeekeeperVerificationStatus;
import com.honeychain.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

public interface AdminBeekeeperService {

    PageResponse<AdminBeekeeperResponse> getBeekeepers(BeekeeperVerificationStatus status, String search,
            Pageable pageable);

    AdminBeekeeperResponse getBeekeeperDetails(Long id);

    AdminBeekeeperResponse updateBeekeeperStatus(Long id, AdminBeekeeperStatusUpdateRequest request);
}
