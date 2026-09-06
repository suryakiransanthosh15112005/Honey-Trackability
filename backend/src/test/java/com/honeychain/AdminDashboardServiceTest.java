package com.honeychain;

import com.honeychain.admin.dto.AdminDashboardResponse;
import com.honeychain.admin.service.impl.AdminDashboardServiceImpl;
import com.honeychain.batch.entity.BatchStatus;
import com.honeychain.batch.repository.HoneyBatchRepository;
import com.honeychain.beekeeper.entity.BeekeeperVerificationStatus;
import com.honeychain.beekeeper.repository.BeekeeperProfileRepository;
import com.honeychain.hive.entity.HiveStatus;
import com.honeychain.hive.repository.HiveRepository;
import com.honeychain.marketplace.repository.ProductRepository;
import com.honeychain.order.entity.OrderStatus;
import com.honeychain.order.repository.OrderRepository;
import com.honeychain.verification.entity.VerificationRiskLevel;
import com.honeychain.verification.repository.VerificationHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminDashboardServiceTest {

    @Mock
    private BeekeeperProfileRepository beekeeperProfileRepository;

    @Mock
    private HiveRepository hiveRepository;

    @Mock
    private HoneyBatchRepository honeyBatchRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private VerificationHistoryRepository verificationHistoryRepository;

    private AdminDashboardServiceImpl adminDashboardService;

    @BeforeEach
    void setUp() {
        adminDashboardService = new AdminDashboardServiceImpl(
                beekeeperProfileRepository,
                hiveRepository,
                honeyBatchRepository,
                productRepository,
                orderRepository,
                verificationHistoryRepository);
    }

    @Test
    @DisplayName("Admin dashboard calculates real-time KPI metrics accurately")
    void testGetDashboardStats() {
        when(beekeeperProfileRepository.count()).thenReturn(125L);
        when(beekeeperProfileRepository.countByVerificationStatus(BeekeeperVerificationStatus.PENDING)).thenReturn(8L);
        when(beekeeperProfileRepository.countByVerificationStatus(BeekeeperVerificationStatus.APPROVED))
                .thenReturn(117L);

        when(hiveRepository.count()).thenReturn(430L);
        when(hiveRepository.countByStatus(HiveStatus.ACTIVE)).thenReturn(392L);

        when(honeyBatchRepository.count()).thenReturn(1042L);
        when(honeyBatchRepository.countByStatus(BatchStatus.PURE)).thenReturn(928L);
        when(honeyBatchRepository.countByStatus(BatchStatus.UNDER_REVIEW)).thenReturn(42L);
        when(honeyBatchRepository.countByStatus(BatchStatus.FAILED)).thenReturn(72L);
        when(honeyBatchRepository.sumTotalHoneyProducedKg()).thenReturn(8245.5);

        when(productRepository.countByIsActiveTrue()).thenReturn(310L);

        when(orderRepository.count()).thenReturn(512L);
        when(orderRepository.countByOrderStatus(OrderStatus.DELIVERED)).thenReturn(431L);

        when(honeyBatchRepository.countByStatus(BatchStatus.SENT_FOR_TESTING)).thenReturn(17L);
        when(verificationHistoryRepository.countByRiskLevel(VerificationRiskLevel.HIGH_RISK)).thenReturn(5L);

        AdminDashboardResponse res = adminDashboardService.getDashboardStats();

        assertNotNull(res);
        assertEquals(125L, res.getTotalBeekeepers());
        assertEquals(8L, res.getPendingBeekeepers());
        assertEquals(117L, res.getApprovedBeekeepers());
        assertEquals(430L, res.getTotalHives());
        assertEquals(392L, res.getActiveHives());
        assertEquals(1042L, res.getTotalBatches());
        assertEquals(928L, res.getPureBatches());
        assertEquals(42L, res.getUnderReviewBatches());
        assertEquals(72L, res.getFailedBatches());
        assertEquals(8245.5, res.getTotalHoneyProducedKg());
        assertEquals(310L, res.getActiveProducts());
        assertEquals(512L, res.getTotalOrders());
        assertEquals(431L, res.getCompletedOrders());
        assertEquals(17L, res.getPendingLabTests());
        assertEquals(5L, res.getHighRiskVerificationBatches());
    }
}
