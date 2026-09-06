package com.honeychain.admin.service;

import com.honeychain.admin.dto.AdminHiveResponse;
import com.honeychain.common.dto.PageResponse;
import com.honeychain.hive.entity.HiveStatus;
import org.springframework.data.domain.Pageable;

public interface AdminHiveService {

    PageResponse<AdminHiveResponse> getHives(HiveStatus status, String search, Pageable pageable);
}
