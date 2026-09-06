package com.honeychain.admin.service;

import com.honeychain.admin.dto.PurityAnalyticsResponse;
import com.honeychain.admin.dto.ProductionTrendResponse;
import com.honeychain.admin.dto.RegionalAnalyticsResponse;
import com.honeychain.admin.dto.SalesAnalyticsResponse;
import com.honeychain.admin.dto.VerificationRiskAnalyticsResponse;

import java.util.List;

public interface AdminAnalyticsService {

    PurityAnalyticsResponse getPurityAnalytics();

    List<RegionalAnalyticsResponse> getRegionalAnalytics();

    List<ProductionTrendResponse> getProductionTrend();

    List<SalesAnalyticsResponse> getSalesAnalytics();

    VerificationRiskAnalyticsResponse getVerificationRiskAnalytics();
}
