package com.honeychain.admin.dto;

public class AdminDashboardResponse {

    private long totalBeekeepers;
    private long pendingBeekeepers;
    private long approvedBeekeepers;
    private long totalHives;
    private long activeHives;
    private long totalBatches;
    private long pureBatches;
    private long underReviewBatches;
    private long failedBatches;
    private double totalHoneyProducedKg;
    private long activeProducts;
    private long totalOrders;
    private long completedOrders;
    private long pendingLabTests;
    private long highRiskVerificationBatches;

    public AdminDashboardResponse() {
    }

    public long getTotalBeekeepers() {
        return totalBeekeepers;
    }

    public void setTotalBeekeepers(long totalBeekeepers) {
        this.totalBeekeepers = totalBeekeepers;
    }

    public long getPendingBeekeepers() {
        return pendingBeekeepers;
    }

    public void setPendingBeekeepers(long pendingBeekeepers) {
        this.pendingBeekeepers = pendingBeekeepers;
    }

    public long getApprovedBeekeepers() {
        return approvedBeekeepers;
    }

    public void setApprovedBeekeepers(long approvedBeekeepers) {
        this.approvedBeekeepers = approvedBeekeepers;
    }

    public long getTotalHives() {
        return totalHives;
    }

    public void setTotalHives(long totalHives) {
        this.totalHives = totalHives;
    }

    public long getActiveHives() {
        return activeHives;
    }

    public void setActiveHives(long activeHives) {
        this.activeHives = activeHives;
    }

    public long getTotalBatches() {
        return totalBatches;
    }

    public void setTotalBatches(long totalBatches) {
        this.totalBatches = totalBatches;
    }

    public long getPureBatches() {
        return pureBatches;
    }

    public void setPureBatches(long pureBatches) {
        this.pureBatches = pureBatches;
    }

    public long getUnderReviewBatches() {
        return underReviewBatches;
    }

    public void setUnderReviewBatches(long underReviewBatches) {
        this.underReviewBatches = underReviewBatches;
    }

    public long getFailedBatches() {
        return failedBatches;
    }

    public void setFailedBatches(long failedBatches) {
        this.failedBatches = failedBatches;
    }

    public double getTotalHoneyProducedKg() {
        return totalHoneyProducedKg;
    }

    public void setTotalHoneyProducedKg(double totalHoneyProducedKg) {
        this.totalHoneyProducedKg = totalHoneyProducedKg;
    }

    public long getActiveProducts() {
        return activeProducts;
    }

    public void setActiveProducts(long activeProducts) {
        this.activeProducts = activeProducts;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public long getCompletedOrders() {
        return completedOrders;
    }

    public void setCompletedOrders(long completedOrders) {
        this.completedOrders = completedOrders;
    }

    public long getPendingLabTests() {
        return pendingLabTests;
    }

    public void setPendingLabTests(long pendingLabTests) {
        this.pendingLabTests = pendingLabTests;
    }

    public long getHighRiskVerificationBatches() {
        return highRiskVerificationBatches;
    }

    public void setHighRiskVerificationBatches(long highRiskVerificationBatches) {
        this.highRiskVerificationBatches = highRiskVerificationBatches;
    }
}
