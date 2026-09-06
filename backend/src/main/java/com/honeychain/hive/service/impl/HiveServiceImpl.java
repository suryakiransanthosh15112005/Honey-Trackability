package com.honeychain.hive.service.impl;

import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.beekeeper.repository.BeekeeperProfileRepository;
import com.honeychain.common.exception.BadRequestException;
import com.honeychain.common.exception.ResourceNotFoundException;
import com.honeychain.common.exception.UnauthorizedException;
import com.honeychain.hive.dto.HiveCreateRequest;
import com.honeychain.hive.dto.HiveResponse;
import com.honeychain.hive.dto.HiveStatusUpdateRequest;
import com.honeychain.hive.dto.HiveUpdateRequest;
import com.honeychain.hive.entity.Hive;
import com.honeychain.hive.entity.HiveStatus;
import com.honeychain.hive.mapper.HiveMapper;
import com.honeychain.hive.repository.HiveRepository;
import com.honeychain.hive.service.HiveService;
import com.honeychain.user.entity.User;
import com.honeychain.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HiveServiceImpl implements HiveService {

    private static final Logger logger = LoggerFactory.getLogger(HiveServiceImpl.class);

    private final HiveRepository hiveRepository;
    private final BeekeeperProfileRepository beekeeperProfileRepository;
    private final UserService userService;
    private final HiveMapper hiveMapper;

    public HiveServiceImpl(HiveRepository hiveRepository,
            BeekeeperProfileRepository beekeeperProfileRepository,
            UserService userService,
            HiveMapper hiveMapper) {
        this.hiveRepository = hiveRepository;
        this.beekeeperProfileRepository = beekeeperProfileRepository;
        this.userService = userService;
        this.hiveMapper = hiveMapper;
    }

    @Override
    @Transactional
    public HiveResponse createHive(String phoneNumber, HiveCreateRequest request) {
        BeekeeperProfile profile = getBeekeeperProfileOrThrow(phoneNumber);
        String hiveCode = generateUniqueHiveCode();
        Hive hive = hiveMapper.toEntity(request, profile.getId(), hiveCode);
        Hive saved = hiveRepository.save(hive);
        logger.info("Created hive {} for beekeeper profile {}", saved.getHiveCode(), profile.getId());
        return hiveMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HiveResponse> getMyHives(String phoneNumber) {
        User user = userService.findEntityByPhoneNumber(phoneNumber);
        BeekeeperProfile profile = beekeeperProfileRepository.findByUserId(user.getId()).orElse(null);
        if (profile == null) {
            return List.of();
        }
        return hiveRepository.findAllByBeekeeperProfileId(profile.getId())
                .stream()
                .map(hiveMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public HiveResponse getMyHive(String phoneNumber, Long hiveId) {
        BeekeeperProfile profile = getBeekeeperProfileOrThrow(phoneNumber);
        Hive hive = hiveRepository.findByIdAndBeekeeperProfileId(hiveId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Hive", "id", hiveId));
        return hiveMapper.toResponse(hive);
    }

    @Override
    @Transactional
    public HiveResponse updateHive(String phoneNumber, Long hiveId, HiveUpdateRequest request) {
        BeekeeperProfile profile = getBeekeeperProfileOrThrow(phoneNumber);
        Hive hive = hiveRepository.findByIdAndBeekeeperProfileId(hiveId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Hive", "id", hiveId));
        hiveMapper.updateEntity(hive, request);
        Hive updated = hiveRepository.save(hive);
        logger.info("Updated hive {} for beekeeper profile {}", updated.getHiveCode(), profile.getId());
        return hiveMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public HiveResponse updateHiveStatus(String phoneNumber, Long hiveId, HiveStatusUpdateRequest request) {
        BeekeeperProfile profile = getBeekeeperProfileOrThrow(phoneNumber);
        Hive hive = hiveRepository.findByIdAndBeekeeperProfileId(hiveId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Hive", "id", hiveId));

        HiveStatus newStatus = request.getStatus();
        // ALERT is an IoT-driven transition — beekeepers cannot set it manually
        if (newStatus == HiveStatus.ALERT) {
            throw new BadRequestException("ALERT status can only be set by the IoT monitoring system");
        }

        hive.setStatus(newStatus);
        Hive updated = hiveRepository.save(hive);
        logger.info("Status of hive {} changed to {} by beekeeper profile {}", updated.getHiveCode(), newStatus,
                profile.getId());
        return hiveMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public long countMyHives(String phoneNumber) {
        User user = userService.findEntityByPhoneNumber(phoneNumber);
        BeekeeperProfile profile = beekeeperProfileRepository.findByUserId(user.getId()).orElse(null);
        if (profile == null) {
            return 0;
        }
        return hiveRepository.countByBeekeeperProfileId(profile.getId());
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private BeekeeperProfile getBeekeeperProfileOrThrow(String phoneNumber) {
        User user = userService.findEntityByPhoneNumber(phoneNumber);
        return beekeeperProfileRepository.findByUserId(user.getId())
                .orElseThrow(
                        () -> new BadRequestException("Please complete your beekeeper profile before managing hives"));
    }

    private String generateUniqueHiveCode() {
        for (int attempt = 0; attempt < 100; attempt++) {
            long count = hiveRepository.count();
            String code = String.format("HIVE-%04d", count + attempt + 1);
            if (!hiveRepository.existsByHiveCode(code)) {
                return code;
            }
        }
        throw new BadRequestException("Unable to generate a unique hive code. Please try again.");
    }
}
