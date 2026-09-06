package com.honeychain.batch.service.impl;

import com.honeychain.batch.dto.HoneyBatchCreateRequest;
import com.honeychain.batch.dto.HoneyBatchResponse;
import com.honeychain.batch.dto.HoneyBatchUpdateRequest;
import com.honeychain.batch.entity.BatchCreationRequest;
import com.honeychain.batch.entity.BatchStatus;
import com.honeychain.batch.entity.HoneyBatch;
import com.honeychain.batch.mapper.HoneyBatchMapper;
import com.honeychain.batch.repository.BatchCreationRequestRepository;
import com.honeychain.batch.repository.HoneyBatchRepository;
import com.honeychain.batch.service.HoneyBatchService;
import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.beekeeper.repository.BeekeeperProfileRepository;
import com.honeychain.blockchain.service.BlockchainService;
import com.honeychain.common.dto.PageResponse;
import com.honeychain.common.exception.BadRequestException;
import com.honeychain.common.exception.ResourceNotFoundException;
import com.honeychain.common.service.StorageService;
import com.honeychain.hive.entity.Hive;
import com.honeychain.hive.entity.HiveStatus;
import com.honeychain.hive.repository.HiveRepository;
import com.honeychain.user.entity.User;
import com.honeychain.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class HoneyBatchServiceImpl implements HoneyBatchService {

    private static final Logger logger = LoggerFactory.getLogger(HoneyBatchServiceImpl.class);

    private final HoneyBatchRepository honeyBatchRepository;
    private final BeekeeperProfileRepository beekeeperProfileRepository;
    private final HiveRepository hiveRepository;
    private final UserService userService;
    private final StorageService storageService;
    private final BlockchainService blockchainService;
    private final HoneyBatchMapper honeyBatchMapper;
    private final BatchCreationRequestRepository batchCreationRequestRepository;

    public HoneyBatchServiceImpl(HoneyBatchRepository honeyBatchRepository,
            BeekeeperProfileRepository beekeeperProfileRepository,
            HiveRepository hiveRepository,
            UserService userService,
            StorageService storageService,
            BlockchainService blockchainService,
            HoneyBatchMapper honeyBatchMapper,
            BatchCreationRequestRepository batchCreationRequestRepository) {
        this.honeyBatchRepository = honeyBatchRepository;
        this.beekeeperProfileRepository = beekeeperProfileRepository;
        this.hiveRepository = hiveRepository;
        this.userService = userService;
        this.storageService = storageService;
        this.blockchainService = blockchainService;
        this.honeyBatchMapper = honeyBatchMapper;
        this.batchCreationRequestRepository = batchCreationRequestRepository;
    }

    @Override
    @Transactional
    public HoneyBatchResponse createBatch(String phoneNumber, HoneyBatchCreateRequest request,
            MultipartFile photo, String idempotencyKey) {
        BeekeeperProfile profile = getBeekeeperProfileOrThrow(phoneNumber);

        // ── Idempotency check ─────────────────────────────────────────────────────
        // If an idempotency key is present and a batch was already successfully
        // created with this key by the same beekeeper, return that batch immediately
        // without creating a duplicate. This covers the case where the client retried
        // after a network failure even though the backend had already persisted the
        // batch.
        if (StringUtils.hasText(idempotencyKey)) {
            var existing = batchCreationRequestRepository
                    .findByIdempotencyKeyAndBeekeeperProfileId(idempotencyKey, profile.getId());
            if (existing.isPresent()) {
                String existingBatchId = existing.get().getBatchId();
                logger.info("Idempotency hit for key {} → returning existing batch {}",
                        idempotencyKey, existingBatchId);
                HoneyBatch existingBatch = honeyBatchRepository
                        .findByBatchIdAndBeekeeperProfileId(existingBatchId, profile.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("HoneyBatch", "batchId", existingBatchId));
                Hive hive = hiveRepository.findById(existingBatch.getHiveId()).orElse(null);
                return honeyBatchMapper.toResponse(existingBatch, hive);
            }
        }

        // ── Normal batch creation ─────────────────────────────────────────────────
        Hive hive = hiveRepository.findByIdAndBeekeeperProfileId(request.getHiveId(), profile.getId())
                .orElseThrow(() -> new BadRequestException("Hive not found or does not belong to your account"));

        if (hive.getStatus() != HiveStatus.ACTIVE) {
            throw new BadRequestException(
                    "Hive is not currently available for harvesting (Status: " + hive.getStatus() + ")");
        }

        if (request.getHarvestDate().isAfter(LocalDate.now())) {
            throw new BadRequestException("Harvest date cannot be in the future");
        }

        String photoUrl = null;
        if (photo != null && !photo.isEmpty()) {
            photoUrl = storageService.storeFile(photo, "batches");
        }

        String batchId = generateUniqueBatchId(request.getHarvestDate().getYear());
        HoneyBatch batch = honeyBatchMapper.toEntity(request, profile.getId(), batchId, photoUrl);
        HoneyBatch saved = honeyBatchRepository.save(batch);

        // Automatically record immutable hash on blockchain ledger (Phase 6 —
        // unchanged)
        blockchainService.recordBatch(saved);

        // ── Persist idempotency record if a key was provided ────────────────────
        if (StringUtils.hasText(idempotencyKey)) {
            BatchCreationRequest idempotencyRecord = new BatchCreationRequest(idempotencyKey, saved.getBatchId(),
                    profile.getId());
            batchCreationRequestRepository.save(idempotencyRecord);
            logger.info("Stored idempotency record: key {} → batch {}", idempotencyKey, saved.getBatchId());
        }

        logger.info("Created honey batch {} from hive {} for beekeeper profile {} and recorded on blockchain",
                saved.getBatchId(), hive.getHiveCode(), profile.getId());

        return honeyBatchMapper.toResponse(saved, hive);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<HoneyBatchResponse> getMyBatches(String phoneNumber, int page, int size, String sort) {
        User user = userService.findEntityByPhoneNumber(phoneNumber);
        BeekeeperProfile profile = beekeeperProfileRepository.findByUserId(user.getId()).orElse(null);
        if (profile == null) {
            return new PageResponse<>(List.of(), 0, size, 0, 0, true);
        }

        Sort sorting = Sort.by(Sort.Direction.DESC, "createdAt");
        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",");
            String property = parts[0].trim();
            Sort.Direction direction = parts.length > 1 && parts[1].trim().equalsIgnoreCase("asc")
                    ? Sort.Direction.ASC
                    : Sort.Direction.DESC;
            sorting = Sort.by(direction, property);
        }

        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sorting);
        Page<HoneyBatch> batchPage = honeyBatchRepository.findAllByBeekeeperProfileId(profile.getId(), pageable);

        Map<Long, Hive> hiveMap = hiveRepository.findAllByBeekeeperProfileId(profile.getId()).stream()
                .collect(Collectors.toMap(Hive::getId, h -> h));

        List<HoneyBatchResponse> content = batchPage.getContent().stream()
                .map(b -> honeyBatchMapper.toResponse(b, hiveMap.get(b.getHiveId())))
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                batchPage.getNumber(),
                batchPage.getSize(),
                batchPage.getTotalElements(),
                batchPage.getTotalPages(),
                batchPage.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public HoneyBatchResponse getMyBatch(String phoneNumber, String batchId) {
        BeekeeperProfile profile = getBeekeeperProfileOrThrow(phoneNumber);
        HoneyBatch batch = honeyBatchRepository.findByBatchIdAndBeekeeperProfileId(batchId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("HoneyBatch", "batchId", batchId));

        Hive hive = hiveRepository.findById(batch.getHiveId()).orElse(null);
        return honeyBatchMapper.toResponse(batch, hive);
    }

    @Override
    @Transactional
    public HoneyBatchResponse updateBatch(String phoneNumber, String batchId, HoneyBatchUpdateRequest request,
            MultipartFile photo) {
        BeekeeperProfile profile = getBeekeeperProfileOrThrow(phoneNumber);
        HoneyBatch batch = honeyBatchRepository.findByBatchIdAndBeekeeperProfileId(batchId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("HoneyBatch", "batchId", batchId));

        if (batch.getStatus() != BatchStatus.CREATED) {
            throw new BadRequestException(
                    "Only batches in CREATED status can be modified (Current status: " + batch.getStatus() + ")");
        }

        if (request.getHarvestDate().isAfter(LocalDate.now())) {
            throw new BadRequestException("Harvest date cannot be in the future");
        }

        String photoUrl = null;
        if (photo != null && !photo.isEmpty()) {
            if (batch.getPhotoUrl() != null) {
                storageService.deleteFile(batch.getPhotoUrl());
            }
            photoUrl = storageService.storeFile(photo, "batches");
        }

        honeyBatchMapper.updateEntity(batch, request, photoUrl);
        HoneyBatch updated = honeyBatchRepository.save(batch);

        Hive hive = hiveRepository.findById(batch.getHiveId()).orElse(null);
        logger.info("Updated batch {} for beekeeper profile {}", updated.getBatchId(), profile.getId());

        return honeyBatchMapper.toResponse(updated, hive);
    }

    @Override
    @Transactional
    public HoneyBatchResponse sendForTesting(String phoneNumber, String batchId) {
        BeekeeperProfile profile = getBeekeeperProfileOrThrow(phoneNumber);
        HoneyBatch batch = honeyBatchRepository.findByBatchIdAndBeekeeperProfileId(batchId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("HoneyBatch", "batchId", batchId));

        if (batch.getStatus() != BatchStatus.CREATED) {
            throw new BadRequestException("Only batches in CREATED status can be sent for testing (Current status: "
                    + batch.getStatus() + ")");
        }

        batch.setStatus(BatchStatus.SENT_FOR_TESTING);
        HoneyBatch updated = honeyBatchRepository.save(batch);

        Hive hive = hiveRepository.findById(batch.getHiveId()).orElse(null);
        logger.info("Batch {} transition: CREATED -> SENT_FOR_TESTING by beekeeper profile {}",
                batchId, profile.getId());

        return honeyBatchMapper.toResponse(updated, hive);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getBatchStats(String phoneNumber) {
        User user = userService.findEntityByPhoneNumber(phoneNumber);
        BeekeeperProfile profile = beekeeperProfileRepository.findByUserId(user.getId()).orElse(null);
        if (profile == null) {
            return Map.of("total", 0L, "created", 0L, "sentForTesting", 0L);
        }
        List<HoneyBatch> allBatches = honeyBatchRepository.findAllByBeekeeperProfileId(profile.getId());

        long total = allBatches.size();
        long created = allBatches.stream().filter(b -> b.getStatus() == BatchStatus.CREATED).count();
        long sentForTesting = allBatches.stream().filter(b -> b.getStatus() == BatchStatus.SENT_FOR_TESTING).count();

        return Map.of(
                "total", total,
                "created", created,
                "sentForTesting", sentForTesting);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private BeekeeperProfile getBeekeeperProfileOrThrow(String phoneNumber) {
        User user = userService.findEntityByPhoneNumber(phoneNumber);
        return beekeeperProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BadRequestException(
                        "Please complete your beekeeper profile before managing honey batches"));
    }

    private String generateUniqueBatchId(int year) {
        for (int attempt = 0; attempt < 50; attempt++) {
            String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase(Locale.ROOT);
            String candidate = String.format("HC-%d-%s", year, randomPart);
            if (!honeyBatchRepository.existsByBatchId(candidate)) {
                return candidate;
            }
        }
        throw new BadRequestException("Failed to generate a unique batch ID. Please try again.");
    }
}
