package com.honeychain.admin.service;

import com.honeychain.admin.dto.AdminLabSummaryResponse;
import com.honeychain.common.dto.PageResponse;
import com.honeychain.lab.entity.LabTestResult;
import org.springframework.data.domain.Pageable;

public interface AdminLabService {

    PageResponse<AdminLabSummaryResponse> getLabTests(LabTestResult result, Pageable pageable);
}
