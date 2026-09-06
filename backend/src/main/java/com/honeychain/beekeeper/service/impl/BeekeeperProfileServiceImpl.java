package com.honeychain.beekeeper.service.impl;

import com.honeychain.beekeeper.dto.BeekeeperProfileRequest;
import com.honeychain.beekeeper.dto.BeekeeperProfileResponse;
import com.honeychain.beekeeper.dto.BeekeeperProfileStatusResponse;
import com.honeychain.beekeeper.dto.BeekeeperProfileUpdateRequest;
import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.beekeeper.mapper.BeekeeperProfileMapper;
import com.honeychain.beekeeper.repository.BeekeeperProfileRepository;
import com.honeychain.beekeeper.service.BeekeeperProfileService;
import com.honeychain.common.exception.ConflictException;
import com.honeychain.common.exception.ResourceNotFoundException;
import com.honeychain.user.entity.User;
import com.honeychain.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BeekeeperProfileServiceImpl implements BeekeeperProfileService {

    private static final Logger logger = LoggerFactory.getLogger(BeekeeperProfileServiceImpl.class);

    private final BeekeeperProfileRepository beekeeperProfileRepository;
    private final UserService userService;
    private final BeekeeperProfileMapper mapper;

    public BeekeeperProfileServiceImpl(BeekeeperProfileRepository beekeeperProfileRepository,
                                       UserService userService,
                                       BeekeeperProfileMapper mapper) {
        this.beekeeperProfileRepository = beekeeperProfileRepository;
        this.userService = userService;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public BeekeeperProfileResponse createProfile(String phoneNumber, BeekeeperProfileRequest request) {
        logger.info("Creating beekeeper profile for user: {}", phoneNumber);

        User user = userService.findEntityByPhoneNumber(phoneNumber);

        // 1. Verify if user already has a profile
        if (beekeeperProfileRepository.existsByUserId(user.getId())) {
            throw new ConflictException("Beekeeper profile already exists for this account");
        }

        // 2. Verify KVIC ID uniqueness
        String cleanKvicId = request.getKvicId().trim();
        if (beekeeperProfileRepository.existsByKvicId(cleanKvicId)) {
            throw new ConflictException("KVIC ID '" + cleanKvicId + "' is already registered");
        }

        // 3. Map & Save
        BeekeeperProfile profile = mapper.toEntity(request, user.getId());
        BeekeeperProfile saved = beekeeperProfileRepository.save(profile);
        logger.info("Created beekeeper profile ID: {} for user: {}", saved.getId(), phoneNumber);

        return mapper.toResponse(saved, phoneNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public BeekeeperProfileResponse getProfile(String phoneNumber) {
        logger.debug("Fetching beekeeper profile for user: {}", phoneNumber);

        User user = userService.findEntityByPhoneNumber(phoneNumber);
        BeekeeperProfile profile = beekeeperProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Beekeeper profile not found. Please complete onboarding."));

        return mapper.toResponse(profile, phoneNumber);
    }

    @Override
    @Transactional
    public BeekeeperProfileResponse updateProfile(String phoneNumber, BeekeeperProfileUpdateRequest request) {
        logger.info("Updating beekeeper profile for user: {}", phoneNumber);

        User user = userService.findEntityByPhoneNumber(phoneNumber);
        BeekeeperProfile profile = beekeeperProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Beekeeper profile not found"));

        mapper.updateEntity(profile, request);
        BeekeeperProfile updated = beekeeperProfileRepository.save(profile);

        return mapper.toResponse(updated, phoneNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public BeekeeperProfileStatusResponse getProfileStatus(String phoneNumber) {
        User user = userService.findEntityByPhoneNumber(phoneNumber);
        return beekeeperProfileRepository.findByUserId(user.getId())
                .map(mapper::toStatusResponse)
                .orElse(BeekeeperProfileStatusResponse.notCompleted());
    }
}
