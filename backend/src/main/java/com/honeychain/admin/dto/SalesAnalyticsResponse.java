package com.honeychain.admin.dto;

public class SalesAnalyticsResponse {

    private String month;
    private long totalOrders;
    private long completedOrders;
    private long cancelledOrders;
    private double totalRevenue;

    public SalesAnalyticsResponse() {
    }

    public SalesAnalyticsResponse(String month, long totalOrders, long completedOrders, long cancelledOrders,
            double totalRevenue) {
        this.month = month;
        this.totalOrders = totalOrders;
        this.completedOrders = completedOrders;
        this.cancelledOrders = cancelledOrders;
        this.totalRevenue = totalRevenue;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
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

    public long getCancelledOrders() {
        return cancelledOrders;
    }

    public void setCancelledOrders(long cancelledOrders) {
        this.cancelledOrders = cancelledOrders;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
