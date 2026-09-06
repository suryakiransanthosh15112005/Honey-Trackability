package com.honeychain;

import com.honeychain.admin.dto.AdminBeekeeperResponse;
import com.honeychain.admin.dto.AdminBeekeeperStatusUpdateRequest;
import com.honeychain.admin.service.impl.AdminBeekeeperServiceImpl;
import com.honeychain.batch.repository.HoneyBatchRepository;
import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.beekeeper.entity.BeekeeperVerificationStatus;
import com.honeychain.beekeeper.repository.BeekeeperProfileRepository;
import com.honeychain.common.exception.BadRequestException;
import com.honeychain.hive.repository.HiveRepository;
import com.honeychain.marketplace.repository.ProductRepository;
import com.honeychain.review.repository.ReviewRepository;
import com.honeychain.user.entity.Role;
import com.honeychain.user.entity.User;
import com.honeychain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminBeekeeperServiceTest {

    @Mock
    private BeekeeperProfileRepository beekeeperProfileRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HiveRepository hiveRepository;

    @Mock
    private HoneyBatchRepository honeyBatchRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ReviewRepository reviewRepository;

    private AdminBeekeeperServiceImpl adminBeekeeperService;

    private BeekeeperProfile profile;
    private User user;

    @BeforeEach
    void setUp() {
        adminBeekeeperService = new AdminBeekeeperServiceImpl(
                beekeeperProfileRepository,
                userRepository,
                hiveRepository,
                honeyBatchRepository,
                productRepository,
                reviewRepository);

        user = new User("9876543213", "pass", Role.BEEKEEPER);
        user.setId(2L);

        profile = new BeekeeperProfile();
        profile.setId(10L);
        profile.setUserId(2L);
        profile.setKvicId("KVIC-2026-001");
        profile.setName("Suresh Patel");
        profile.setVillage("Nilgiris");
        profile.setVerificationStatus(BeekeeperVerificationStatus.PENDING);
    }

    @Test
    @DisplayName("Admin can approve a PENDING beekeeper")
    void testApprovePendingBeekeeper() {
        AdminBeekeeperStatusUpdateRequest req = new AdminBeekeeperStatusUpdateRequest(
                BeekeeperVerificationStatus.APPROVED);

        when(beekeeperProfileRepository.findById(10L)).thenReturn(Optional.of(profile));
        when(beekeeperProfileRepository.save(any(BeekeeperProfile.class))).thenAnswer(i -> i.getArgument(0));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(hiveRepository.countByBeekeeperProfileId(10L)).thenReturn(4L);
        when(honeyBatchRepository.countByBeekeeperProfileId(10L)).thenReturn(8L);
        when(productRepository.findAllByBeekeeperProfileId(10L, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(Collections.emptyList()));
        when(reviewRepository.getAverageRatingByBeekeeperId(10L)).thenReturn(4.8);

        AdminBeekeeperResponse response = adminBeekeeperService.updateBeekeeperStatus(10L, req);

        assertNotNull(response);
        assertEquals(BeekeeperVerificationStatus.APPROVED, response.getVerificationStatus());
        assertEquals("Suresh Patel", response.getName());
        assertEquals(4L, response.getHiveCount());
    }

    @Test
    @DisplayName("Admin can reject a PENDING beekeeper")
    void testRejectPendingBeekeeper() {
        AdminBeekeeperStatusUpdateRequest req = new AdminBeekeeperStatusUpdateRequest(
                BeekeeperVerificationStatus.REJECTED);

        when(beekeeperProfileRepository.findById(10L)).thenReturn(Optional.of(profile));
        when(beekeeperProfileRepository.save(any(BeekeeperProfile.class))).thenAnswer(i -> i.getArgument(0));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(hiveRepository.countByBeekeeperProfileId(10L)).thenReturn(0L);
        when(honeyBatchRepository.countByBeekeeperProfileId(10L)).thenReturn(0L);
        when(productRepository.findAllByBeekeeperProfileId(10L, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(Collections.emptyList()));
        when(reviewRepository.getAverageRatingByBeekeeperId(10L)).thenReturn(0.0);

        AdminBeekeeperResponse response = adminBeekeeperService.updateBeekeeperStatus(10L, req);

        assertNotNull(response);
        assertEquals(BeekeeperVerificationStatus.REJECTED, response.getVerificationStatus());
    }

    @Test
    @DisplayName("Re-approving an already APPROVED beekeeper throws BadRequestException")
    void testReapproveApprovedBeekeeperThrowsException() {
        profile.setVerificationStatus(BeekeeperVerificationStatus.APPROVED);
        AdminBeekeeperStatusUpdateRequest req = new AdminBeekeeperStatusUpdateRequest(
                BeekeeperVerificationStatus.APPROVED);

        when(beekeeperProfileRepository.findById(10L)).thenReturn(Optional.of(profile));

        assertThrows(BadRequestException.class, () -> adminBeekeeperService.updateBeekeeperStatus(10L, req));
    }
}
