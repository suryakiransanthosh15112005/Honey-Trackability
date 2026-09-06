package com.honeychain.hive.service;

import com.honeychain.hive.dto.HiveCreateRequest;
import com.honeychain.hive.dto.HiveResponse;
import com.honeychain.hive.dto.HiveStatusUpdateRequest;
import com.honeychain.hive.dto.HiveUpdateRequest;

import java.util.List;

public interface HiveService {

    HiveResponse createHive(String phoneNumber, HiveCreateRequest request);

    List<HiveResponse> getMyHives(String phoneNumber);

    HiveResponse getMyHive(String phoneNumber, Long hiveId);

    HiveResponse updateHive(String phoneNumber, Long hiveId, HiveUpdateRequest request);

    HiveResponse updateHiveStatus(String phoneNumber, Long hiveId, HiveStatusUpdateRequest request);

    long countMyHives(String phoneNumber);
}
