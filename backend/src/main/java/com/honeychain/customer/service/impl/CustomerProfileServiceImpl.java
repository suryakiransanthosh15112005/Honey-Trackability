package com.honeychain.customer.service.impl;

import com.honeychain.common.exception.ResourceNotFoundException;
import com.honeychain.customer.dto.CustomerProfileRequest;
import com.honeychain.customer.dto.CustomerProfileResponse;
import com.honeychain.customer.dto.CustomerProfileStatusResponse;
import com.honeychain.customer.entity.CustomerProfile;
import com.honeychain.customer.repository.CustomerProfileRepository;
import com.honeychain.customer.service.CustomerProfileService;
import com.honeychain.user.entity.User;
import com.honeychain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomerProfileServiceImpl implements CustomerProfileService {

    private final CustomerProfileRepository customerProfileRepository;
    private final UserRepository userRepository;

    public CustomerProfileServiceImpl(CustomerProfileRepository customerProfileRepository, UserRepository userRepository) {
        this.customerProfileRepository = customerProfileRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerProfileResponse getProfile(String phoneNumber) {
        User user = getUserByPhoneNumber(phoneNumber);
        CustomerProfile profile = customerProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found for user: " + phoneNumber));
        return CustomerProfileResponse.fromEntity(profile, user.getPhoneNumber());
    }

    @Override
    public CustomerProfileResponse createProfile(String phoneNumber, CustomerProfileRequest request) {
        User user = getUserByPhoneNumber(phoneNumber);
        if (customerProfileRepository.existsByUserId(user.getId())) {
            throw new IllegalArgumentException("Customer profile already exists for user");
        }
        CustomerProfile profile = new CustomerProfile(
                user.getId(),
                request.getFullName(),
                request.getEmail(),
                request.getAddress(),
                request.getCity(),
                request.getState(),
                request.getPincode(),
                request.getProfilePhotoUrl()
        );
        CustomerProfile saved = customerProfileRepository.save(profile);
        return CustomerProfileResponse.fromEntity(saved, user.getPhoneNumber());
    }

    @Override
    public CustomerProfileResponse updateProfile(String phoneNumber, CustomerProfileRequest request) {
        User user = getUserByPhoneNumber(phoneNumber);
        CustomerProfile profile = customerProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found for user: " + phoneNumber));

        if (request.getFullName() != null) profile.setFullName(request.getFullName());
        if (request.getEmail() != null) profile.setEmail(request.getEmail());
        if (request.getAddress() != null) profile.setAddress(request.getAddress());
        if (request.getCity() != null) profile.setCity(request.getCity());
        if (request.getState() != null) profile.setState(request.getState());
        if (request.getPincode() != null) profile.setPincode(request.getPincode());
        if (request.getProfilePhotoUrl() != null) profile.setProfilePhotoUrl(request.getProfilePhotoUrl());

        CustomerProfile updated = customerProfileRepository.save(profile);
        return CustomerProfileResponse.fromEntity(updated, user.getPhoneNumber());
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerProfileStatusResponse getStatus(String phoneNumber) {
        User user = getUserByPhoneNumber(phoneNumber);
        return customerProfileRepository.findByUserId(user.getId())
                .map(p -> new CustomerProfileStatusResponse(true, p.getFullName() != null && !p.getFullName().isBlank(), "Customer profile setup complete"))
                .orElseGet(() -> new CustomerProfileStatusResponse(false, false, "Customer profile setup incomplete"));
    }

    private User getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + phoneNumber));
    }
}
