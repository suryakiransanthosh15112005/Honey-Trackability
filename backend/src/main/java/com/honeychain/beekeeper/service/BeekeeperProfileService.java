package com.honeychain.beekeeper.service;

import com.honeychain.beekeeper.dto.BeekeeperProfileRequest;
import com.honeychain.beekeeper.dto.BeekeeperProfileResponse;
import com.honeychain.beekeeper.dto.BeekeeperProfileStatusResponse;
import com.honeychain.beekeeper.dto.BeekeeperProfileUpdateRequest;

public interface BeekeeperProfileService {

    BeekeeperProfileResponse createProfile(String phoneNumber, BeekeeperProfileRequest request);

    BeekeeperProfileResponse getProfile(String phoneNumber);

    BeekeeperProfileResponse updateProfile(String phoneNumber, BeekeeperProfileUpdateRequest request);

    BeekeeperProfileStatusResponse getProfileStatus(String phoneNumber);
}
