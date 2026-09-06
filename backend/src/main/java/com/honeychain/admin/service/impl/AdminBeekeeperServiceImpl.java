package com.honeychain.admin.service.impl;

import com.honeychain.admin.dto.AdminBeekeeperResponse;
import com.honeychain.admin.dto.AdminBeekeeperStatusUpdateRequest;
import com.honeychain.admin.service.AdminBeekeeperService;
import com.honeychain.batch.repository.HoneyBatchRepository;
import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.beekeeper.entity.BeekeeperVerificationStatus;
import com.honeychain.beekeeper.repository.BeekeeperProfileRepository;
import com.honeychain.common.dto.PageResponse;
import com.honeychain.common.exception.BadRequestException;
import com.honeychain.common.exception.ResourceNotFoundException;
import com.honeychain.hive.repository.HiveRepository;
import com.honeychain.marketplace.repository.ProductRepository;
import com.honeychain.notification.entity.NotificationType;
import com.honeychain.notification.event.NotificationEvent;
import com.honeychain.review.repository.ReviewRepository;
import com.honeychain.user.entity.User;
import com.honeychain.user.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminBeekeeperServiceImpl implements AdminBeekeeperService {

    private final BeekeeperProfileRepository beekeeperProfileRepository;
    private final UserRepository userRepository;
    private final HiveRepository hiveRepository;
    private final HoneyBatchRepository honeyBatchRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final ApplicationEventPublisher eventPublisher;

    public AdminBeekeeperServiceImpl(BeekeeperProfileRepository beekeeperProfileRepository,
            UserRepository userRepository,
            HiveRepository hiveRepository,
            HoneyBatchRepository honeyBatchRepository,
            ProductRepository productRepository,
            ReviewRepository reviewRepository) {
        this(beekeeperProfileRepository, userRepository, hiveRepository, honeyBatchRepository, productRepository,
                reviewRepository, event -> {
                });
    }

    @org.springframework.beans.factory.annotation.Autowired
    public AdminBeekeeperServiceImpl(BeekeeperProfileRepository beekeeperProfileRepository,
            UserRepository userRepository,
            HiveRepository hiveRepository,
            HoneyBatchRepository honeyBatchRepository,
            ProductRepository productRepository,
            ReviewRepository reviewRepository,
            ApplicationEventPublisher eventPublisher) {
        this.beekeeperProfileRepository = beekeeperProfileRepository;
        this.userRepository = userRepository;
        this.hiveRepository = hiveRepository;
        this.honeyBatchRepository = honeyBatchRepository;
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AdminBeekeeperResponse> getBeekeepers(BeekeeperVerificationStatus status, String search,
            Pageable pageable) {
        String cleanSearch = (search != null && !search.isBlank()) ? search.trim() : null;
        Page<BeekeeperProfile> page = beekeeperProfileRepository.findAdminBeekeepers(status, cleanSearch, pageable);
        List<AdminBeekeeperResponse> content = page.getContent().stream().map(this::toResponse).toList();
        return PageResponse.of(page, content);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminBeekeeperResponse getBeekeeperDetails(Long id) {
        BeekeeperProfile profile = beekeeperProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beekeeper profile not found with ID: " + id));
        return toResponse(profile);
    }

    @Override
    public AdminBeekeeperResponse updateBeekeeperStatus(Long id, AdminBeekeeperStatusUpdateRequest request) {
        BeekeeperProfile profile = beekeeperProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beekeeper profile not found with ID: " + id));

        BeekeeperVerificationStatus currentStatus = profile.getVerificationStatus();
        BeekeeperVerificationStatus newStatus = request.getStatus();

        if (currentStatus == BeekeeperVerificationStatus.APPROVED
                && newStatus == BeekeeperVerificationStatus.APPROVED) {
            throw new BadRequestException("Beekeeper is already APPROVED.");
        }

        if (currentStatus != BeekeeperVerificationStatus.PENDING && newStatus != currentStatus) {
            if (currentStatus == BeekeeperVerificationStatus.APPROVED
                    && newStatus == BeekeeperVerificationStatus.REJECTED) {
                // Administrative revoking is allowed
            } else if (currentStatus == BeekeeperVerificationStatus.REJECTED) {
                throw new BadRequestException("Cannot update status of an already REJECTED beekeeper.");
            }
        }

        profile.setVerificationStatus(newStatus);
        BeekeeperProfile saved = beekeeperProfileRepository.save(profile);

        // Notify beekeeper of profile status update
        String title = "Profile Status Updated";
        String msg = newStatus == BeekeeperVerificationStatus.APPROVED
                ? "Your HoneyChain beekeeper profile has been approved."
                : "Your HoneyChain beekeeper profile status has been updated to " + newStatus + ".";
        eventPublisher.publishEvent(new NotificationEvent(
                saved.getUserId(),
                title,
                msg,
                NotificationType.PROFILE_STATUS,
                "PROFILE",
                saved.getId().toString(),
                true));

        return toResponse(saved);
    }

    private AdminBeekeeperResponse toResponse(BeekeeperProfile profile) {
        AdminBeekeeperResponse res = new AdminBeekeeperResponse();
        res.setId(profile.getId());
        res.setUserId(profile.getUserId());
        res.setKvicId(profile.getKvicId());
        res.setName(profile.getName());
        res.setVillage(profile.getVillage());
        res.setPhotoUrl(profile.getPhotoUrl());
        res.setPreferredLanguage(profile.getPreferredLanguage());
        res.setVerificationStatus(profile.getVerificationStatus());
        res.setCreatedAt(profile.getCreatedAt());

        userRepository.findById(profile.getUserId()).ifPresent(u -> res.setPhoneNumber(u.getPhoneNumber()));

        res.setHiveCount(hiveRepository.countByBeekeeperProfileId(profile.getId()));
        res.setBatchCount(honeyBatchRepository.countByBeekeeperProfileId(profile.getId()));
        res.setProductCount(
                productRepository.findAllByBeekeeperProfileId(profile.getId(), Pageable.unpaged()).getTotalElements());

        Double avgRating = reviewRepository.getAverageRatingByBeekeeperId(profile.getId());
        res.setAverageRating(avgRating != null ? avgRating : 0.0);

        return res;
    }
}
