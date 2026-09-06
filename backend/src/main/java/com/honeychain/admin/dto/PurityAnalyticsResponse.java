package com.honeychain.admin.dto;

public class PurityAnalyticsResponse {

    private long totalTests;
    private long pureCount;
    private long underReviewCount;
    private long failedCount;
    private double passRate;
    private double averagePurityScore;

    public PurityAnalyticsResponse() {
    }

    public PurityAnalyticsResponse(long totalTests, long pureCount, long underReviewCount,
            long failedCount, double passRate, double averagePurityScore) {
        this.totalTests = totalTests;
        this.pureCount = pureCount;
        this.underReviewCount = underReviewCount;
        this.failedCount = failedCount;
        this.passRate = passRate;
        this.averagePurityScore = averagePurityScore;
    }

    public long getTotalTests() {
        return totalTests;
    }

    public void setTotalTests(long totalTests) {
        this.totalTests = totalTests;
    }

    public long getPureCount() {
        return pureCount;
    }

    public void setPureCount(long pureCount) {
        this.pureCount = pureCount;
    }

    public long getUnderReviewCount() {
        return underReviewCount;
    }

    public void setUnderReviewCount(long underReviewCount) {
        this.underReviewCount = underReviewCount;
    }

    public long getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(long failedCount) {
        this.failedCount = failedCount;
    }

    public double getPassRate() {
        return passRate;
    }

    public void setPassRate(double passRate) {
        this.passRate = passRate;
    }

    public double getAveragePurityScore() {
        return averagePurityScore;
    }

    public void setAveragePurityScore(double averagePurityScore) {
        this.averagePurityScore = averagePurityScore;
    }
}
