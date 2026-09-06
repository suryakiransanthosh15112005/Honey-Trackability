package com.honeychain.beekeeper.mapper;

import com.honeychain.beekeeper.dto.BeekeeperProfileRequest;
import com.honeychain.beekeeper.dto.BeekeeperProfileResponse;
import com.honeychain.beekeeper.dto.BeekeeperProfileStatusResponse;
import com.honeychain.beekeeper.dto.BeekeeperProfileUpdateRequest;
import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.beekeeper.entity.BeekeeperVerificationStatus;
import org.springframework.stereotype.Component;

@Component
public class BeekeeperProfileMapper {

    public BeekeeperProfile toEntity(BeekeeperProfileRequest request, Long userId) {
        if (request == null) return null;

        return new BeekeeperProfile(
                userId,
                request.getKvicId() != null ? request.getKvicId().trim() : null,
                request.getName() != null ? request.getName().trim() : null,
                request.getVillage() != null ? request.getVillage().trim() : null,
                request.getPhotoUrl(),
                request.getLatitude(),
                request.getLongitude(),
                request.getPreferredLanguage(),
                BeekeeperVerificationStatus.PENDING
        );
    }

    public void updateEntity(BeekeeperProfile profile, BeekeeperProfileUpdateRequest request) {
        if (profile == null || request == null) return;

        profile.setName(request.getName().trim());
        profile.setVillage(request.getVillage().trim());
        if (request.getPhotoUrl() != null) {
            profile.setPhotoUrl(request.getPhotoUrl());
        }
        profile.setLatitude(request.getLatitude());
        profile.setLongitude(request.getLongitude());
        if (request.getPreferredLanguage() != null) {
            profile.setPreferredLanguage(request.getPreferredLanguage());
        }
    }

    public BeekeeperProfileResponse toResponse(BeekeeperProfile entity, String phoneNumber) {
        if (entity == null) return null;

        return new BeekeeperProfileResponse(
                entity.getId(),
                entity.getUserId(),
                phoneNumber,
                entity.getKvicId(),
                entity.getName(),
                entity.getVillage(),
                entity.getPhotoUrl(),
                entity.getLatitude(),
                entity.getLongitude(),
                entity.getPreferredLanguage(),
                entity.getVerificationStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public BeekeeperProfileStatusResponse toStatusResponse(BeekeeperProfile entity) {
        if (entity == null) {
            return BeekeeperProfileStatusResponse.notCompleted();
        }
        return BeekeeperProfileStatusResponse.completed(
                entity.getVerificationStatus(),
                entity.getKvicId(),
                entity.getName()
        );
    }
}
