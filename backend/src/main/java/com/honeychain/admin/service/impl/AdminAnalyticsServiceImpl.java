package com.honeychain.admin.service.impl;

import com.honeychain.admin.dto.AdminBatchResponse;
import com.honeychain.admin.dto.PurityAnalyticsResponse;
import com.honeychain.admin.dto.ProductionTrendResponse;
import com.honeychain.admin.dto.RegionalAnalyticsResponse;
import com.honeychain.admin.dto.SalesAnalyticsResponse;
import com.honeychain.admin.dto.VerificationRiskAnalyticsResponse;
import com.honeychain.admin.service.AdminAnalyticsService;
import com.honeychain.admin.service.AdminBatchService;
import com.honeychain.batch.entity.HoneyBatch;
import com.honeychain.batch.repository.HoneyBatchRepository;
import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.beekeeper.repository.BeekeeperProfileRepository;
import com.honeychain.hive.entity.Hive;
import com.honeychain.hive.entity.HiveStatus;
import com.honeychain.hive.repository.HiveRepository;
import com.honeychain.lab.entity.LabTest;
import com.honeychain.lab.entity.LabTestResult;
import com.honeychain.lab.repository.LabTestRepository;
import com.honeychain.marketplace.repository.ProductRepository;
import com.honeychain.order.entity.Order;
import com.honeychain.order.entity.OrderStatus;
import com.honeychain.order.repository.OrderRepository;
import com.honeychain.verification.entity.VerificationRiskLevel;
import com.honeychain.verification.repository.VerificationHistoryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AdminAnalyticsServiceImpl implements AdminAnalyticsService {

    private final LabTestRepository labTestRepository;
    private final HoneyBatchRepository honeyBatchRepository;
    private final BeekeeperProfileRepository beekeeperProfileRepository;
    private final HiveRepository hiveRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final VerificationHistoryRepository verificationHistoryRepository;
    private final AdminBatchService adminBatchService;

    public AdminAnalyticsServiceImpl(LabTestRepository labTestRepository,
            HoneyBatchRepository honeyBatchRepository,
            BeekeeperProfileRepository beekeeperProfileRepository,
            HiveRepository hiveRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository,
            VerificationHistoryRepository verificationHistoryRepository,
            AdminBatchService adminBatchService) {
        this.labTestRepository = labTestRepository;
        this.honeyBatchRepository = honeyBatchRepository;
        this.beekeeperProfileRepository = beekeeperProfileRepository;
        this.hiveRepository = hiveRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.verificationHistoryRepository = verificationHistoryRepository;
        this.adminBatchService = adminBatchService;
    }

    @Override
    public PurityAnalyticsResponse getPurityAnalytics() {
        long totalTests = labTestRepository.count();
        long pureCount = labTestRepository.countByResult(LabTestResult.PURE);
        long underReviewCount = labTestRepository.countByResult(LabTestResult.UNDER_REVIEW);
        long failedCount = labTestRepository.countByResult(LabTestResult.FAILED);

        double passRate = (totalTests > 0)
                ? BigDecimal.valueOf(((double) pureCount / totalTests) * 100.0)
                        .setScale(1, RoundingMode.HALF_UP).doubleValue()
                : 0.0;

        Double avgScore = labTestRepository.findAveragePurityScore();
        double averagePurityScore = (avgScore != null)
                ? BigDecimal.valueOf(avgScore).setScale(1, RoundingMode.HALF_UP).doubleValue()
                : 0.0;

        return new PurityAnalyticsResponse(
                totalTests,
                pureCount,
                underReviewCount,
                failedCount,
                passRate,
                averagePurityScore);
    }

    @Override
    public List<RegionalAnalyticsResponse> getRegionalAnalytics() {
        List<BeekeeperProfile> beekeepers = beekeeperProfileRepository.findAll();
        if (beekeepers.isEmpty())
            return Collections.emptyList();

        Map<String, List<BeekeeperProfile>> regionMap = beekeepers.stream()
                .collect(Collectors
                        .groupingBy(b -> (b.getVillage() != null && !b.getVillage().isBlank()) ? b.getVillage()
                                : "Unknown Region"));

        List<RegionalAnalyticsResponse> list = new ArrayList<>();

        for (Map.Entry<String, List<BeekeeperProfile>> entry : regionMap.entrySet()) {
            String region = entry.getKey();
            List<BeekeeperProfile> profiles = entry.getValue();
            List<Long> profileIds = profiles.stream().map(BeekeeperProfile::getId).toList();

            long beekeeperCount = profiles.size();
            long activeHives = 0;
            long batchesCount = 0;
            double honeyProduced = 0.0;
            double totalPurity = 0.0;
            long purityCount = 0;
            long productCount = 0;

            for (Long pid : profileIds) {
                List<Hive> hives = hiveRepository.findAllByBeekeeperProfileId(pid);
                activeHives += hives.stream().filter(h -> h.getStatus() == HiveStatus.ACTIVE).count();

                List<HoneyBatch> batches = honeyBatchRepository.findAllByBeekeeperProfileId(pid);
                batchesCount += batches.size();
                for (HoneyBatch b : batches) {
                    if (b.getQuantityKg() != null) {
                        honeyProduced += b.getQuantityKg().doubleValue();
                    }
                    Optional<LabTest> lt = labTestRepository.findByBatchId(b.getBatchId());
                    if (lt.isPresent() && lt.get().getPurityScore() != null) {
                        totalPurity += lt.get().getPurityScore();
                        purityCount++;
                    }
                }

                productCount += productRepository
                        .findAllByBeekeeperProfileId(pid, org.springframework.data.domain.Pageable.unpaged())
                        .getTotalElements();
            }

            double avgPurity = purityCount > 0
                    ? BigDecimal.valueOf(totalPurity / purityCount).setScale(1, RoundingMode.HALF_UP).doubleValue()
                    : 0.0;

            list.add(new RegionalAnalyticsResponse(
                    region,
                    beekeeperCount,
                    activeHives,
                    batchesCount,
                    BigDecimal.valueOf(honeyProduced).setScale(1, RoundingMode.HALF_UP).doubleValue(),
                    avgPurity,
                    productCount));
        }

        return list;
    }

    @Override
    public List<ProductionTrendResponse> getProductionTrend() {
        List<HoneyBatch> batches = honeyBatchRepository.findAll();
        if (batches.isEmpty())
            return Collections.emptyList();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        Map<String, List<HoneyBatch>> monthlyBatches = new TreeMap<>();

        for (HoneyBatch b : batches) {
            String month = (b.getHarvestDate() != null)
                    ? b.getHarvestDate().format(formatter)
                    : b.getCreatedAt().format(formatter);
            monthlyBatches.computeIfAbsent(month, k -> new ArrayList<>()).add(b);
        }

        List<ProductionTrendResponse> trend = new ArrayList<>();
        for (Map.Entry<String, List<HoneyBatch>> entry : monthlyBatches.entrySet()) {
            String month = entry.getKey();
            List<HoneyBatch> bList = entry.getValue();
            double totalKg = bList.stream()
                    .mapToDouble(b -> b.getQuantityKg() != null ? b.getQuantityKg().doubleValue() : 0.0)
                    .sum();
            trend.add(new ProductionTrendResponse(
                    month,
                    BigDecimal.valueOf(totalKg).setScale(1, RoundingMode.HALF_UP).doubleValue(),
                    bList.size()));
        }

        return trend;
    }

    @Override
    public List<SalesAnalyticsResponse> getSalesAnalytics() {
        List<Order> orders = orderRepository.findAll();
        if (orders.isEmpty())
            return Collections.emptyList();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        Map<String, List<Order>> monthlyOrders = new TreeMap<>();

        for (Order o : orders) {
            String month = o.getCreatedAt().format(formatter);
            monthlyOrders.computeIfAbsent(month, k -> new ArrayList<>()).add(o);
        }

        List<SalesAnalyticsResponse> list = new ArrayList<>();
        for (Map.Entry<String, List<Order>> entry : monthlyOrders.entrySet()) {
            String month = entry.getKey();
            List<Order> oList = entry.getValue();
            long total = oList.size();
            long completed = oList.stream().filter(o -> o.getOrderStatus() == OrderStatus.DELIVERED).count();
            long cancelled = oList.stream().filter(o -> o.getOrderStatus() == OrderStatus.CANCELLED).count();
            double revenue = oList.stream()
                    .filter(o -> o.getOrderStatus() != OrderStatus.CANCELLED)
                    .mapToDouble(o -> o.getTotalAmount() != null ? o.getTotalAmount().doubleValue() : 0.0)
                    .sum();

            list.add(new SalesAnalyticsResponse(
                    month,
                    total,
                    completed,
                    cancelled,
                    BigDecimal.valueOf(revenue).setScale(2, RoundingMode.HALF_UP).doubleValue()));
        }

        return list;
    }

    @Override
    public VerificationRiskAnalyticsResponse getVerificationRiskAnalytics() {
        long normal = verificationHistoryRepository.countByRiskLevel(VerificationRiskLevel.NORMAL);
        long watch = verificationHistoryRepository.countByRiskLevel(VerificationRiskLevel.WATCH);
        long highRisk = verificationHistoryRepository.countByRiskLevel(VerificationRiskLevel.HIGH_RISK);

        // Fetch up to 10 high-risk batch details
        List<AdminBatchResponse> highRiskBatches = adminBatchService.getBatches(null, null, PageRequest.of(0, 100))
                .getContent().stream()
                .filter(b -> b.getRiskLevel() == VerificationRiskLevel.HIGH_RISK
                        || b.getRiskLevel() == VerificationRiskLevel.WATCH)
                .limit(10)
                .toList();

        return new VerificationRiskAnalyticsResponse(normal, watch, highRisk, highRiskBatches);
    }
}
