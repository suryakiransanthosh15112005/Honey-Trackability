package com.honeychain;

import com.honeychain.admin.dto.PurityAnalyticsResponse;
import com.honeychain.admin.service.AdminBatchService;
import com.honeychain.admin.service.impl.AdminAnalyticsServiceImpl;
import com.honeychain.batch.repository.HoneyBatchRepository;
import com.honeychain.beekeeper.repository.BeekeeperProfileRepository;
import com.honeychain.hive.repository.HiveRepository;
import com.honeychain.lab.entity.LabTestResult;
import com.honeychain.lab.repository.LabTestRepository;
import com.honeychain.marketplace.repository.ProductRepository;
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
class AdminAnalyticsServiceTest {

    @Mock
    private LabTestRepository labTestRepository;

    @Mock
    private HoneyBatchRepository honeyBatchRepository;

    @Mock
    private BeekeeperProfileRepository beekeeperProfileRepository;

    @Mock
    private HiveRepository hiveRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private VerificationHistoryRepository verificationHistoryRepository;

    @Mock
    private AdminBatchService adminBatchService;

    private AdminAnalyticsServiceImpl adminAnalyticsService;

    @BeforeEach
    void setUp() {
        adminAnalyticsService = new AdminAnalyticsServiceImpl(
                labTestRepository,
                honeyBatchRepository,
                beekeeperProfileRepository,
                hiveRepository,
                productRepository,
                orderRepository,
                verificationHistoryRepository,
                adminBatchService);
    }

    @Test
    @DisplayName("Purity analytics calculates correct pass rate and average score")
    void testGetPurityAnalytics() {
        when(labTestRepository.count()).thenReturn(100L);
        when(labTestRepository.countByResult(LabTestResult.PURE)).thenReturn(88L);
        when(labTestRepository.countByResult(LabTestResult.UNDER_REVIEW)).thenReturn(8L);
        when(labTestRepository.countByResult(LabTestResult.FAILED)).thenReturn(4L);
        when(labTestRepository.findAveragePurityScore()).thenReturn(94.5);

        PurityAnalyticsResponse res = adminAnalyticsService.getPurityAnalytics();

        assertNotNull(res);
        assertEquals(100L, res.getTotalTests());
        assertEquals(88L, res.getPureCount());
        assertEquals(88.0, res.getPassRate());
        assertEquals(94.5, res.getAveragePurityScore());
    }

    @Test
    @DisplayName("Zero test data produces 0.0 pass rate without division by zero errors")
    void testZeroTestsPurityAnalytics() {
        when(labTestRepository.count()).thenReturn(0L);
        when(labTestRepository.countByResult(LabTestResult.PURE)).thenReturn(0L);
        when(labTestRepository.countByResult(LabTestResult.UNDER_REVIEW)).thenReturn(0L);
        when(labTestRepository.countByResult(LabTestResult.FAILED)).thenReturn(0L);
        when(labTestRepository.findAveragePurityScore()).thenReturn(null);

        PurityAnalyticsResponse res = adminAnalyticsService.getPurityAnalytics();

        assertNotNull(res);
        assertEquals(0L, res.getTotalTests());
        assertEquals(0.0, res.getPassRate());
        assertEquals(0.0, res.getAveragePurityScore());
    }
}
