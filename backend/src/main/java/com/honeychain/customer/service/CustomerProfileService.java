package com.honeychain.customer.service;

import com.honeychain.customer.dto.CustomerProfileRequest;
import com.honeychain.customer.dto.CustomerProfileResponse;
import com.honeychain.customer.dto.CustomerProfileStatusResponse;

public interface CustomerProfileService {

    CustomerProfileResponse getProfile(String phoneNumber);

    CustomerProfileResponse createProfile(String phoneNumber, CustomerProfileRequest request);

    CustomerProfileResponse updateProfile(String phoneNumber, CustomerProfileRequest request);

    CustomerProfileStatusResponse getStatus(String phoneNumber);
}
