package com.honeychain.admin.service.impl;

import com.honeychain.admin.dto.AdminDashboardResponse;
import com.honeychain.admin.service.AdminDashboardService;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final BeekeeperProfileRepository beekeeperProfileRepository;
    private final HiveRepository hiveRepository;
    private final HoneyBatchRepository honeyBatchRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final VerificationHistoryRepository verificationHistoryRepository;

    public AdminDashboardServiceImpl(BeekeeperProfileRepository beekeeperProfileRepository,
            HiveRepository hiveRepository,
            HoneyBatchRepository honeyBatchRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository,
            VerificationHistoryRepository verificationHistoryRepository) {
        this.beekeeperProfileRepository = beekeeperProfileRepository;
        this.hiveRepository = hiveRepository;
        this.honeyBatchRepository = honeyBatchRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.verificationHistoryRepository = verificationHistoryRepository;
    }

    @Override
    public AdminDashboardResponse getDashboardStats() {
        AdminDashboardResponse response = new AdminDashboardResponse();

        response.setTotalBeekeepers(beekeeperProfileRepository.count());
        response.setPendingBeekeepers(
                beekeeperProfileRepository.countByVerificationStatus(BeekeeperVerificationStatus.PENDING));
        response.setApprovedBeekeepers(
                beekeeperProfileRepository.countByVerificationStatus(BeekeeperVerificationStatus.APPROVED));

        response.setTotalHives(hiveRepository.count());
        response.setActiveHives(hiveRepository.countByStatus(HiveStatus.ACTIVE));

        response.setTotalBatches(honeyBatchRepository.count());
        response.setPureBatches(honeyBatchRepository.countByStatus(BatchStatus.PURE));
        response.setUnderReviewBatches(honeyBatchRepository.countByStatus(BatchStatus.UNDER_REVIEW));
        response.setFailedBatches(honeyBatchRepository.countByStatus(BatchStatus.FAILED));
        Double totalHoney = honeyBatchRepository.sumTotalHoneyProducedKg();
        response.setTotalHoneyProducedKg(totalHoney != null ? totalHoney : 0.0);

        response.setActiveProducts(productRepository.countByIsActiveTrue());

        response.setTotalOrders(orderRepository.count());
        response.setCompletedOrders(orderRepository.countByOrderStatus(OrderStatus.DELIVERED));

        response.setPendingLabTests(honeyBatchRepository.countByStatus(BatchStatus.SENT_FOR_TESTING));
        response.setHighRiskVerificationBatches(
                verificationHistoryRepository.countByRiskLevel(VerificationRiskLevel.HIGH_RISK));

        return response;
    }
}
