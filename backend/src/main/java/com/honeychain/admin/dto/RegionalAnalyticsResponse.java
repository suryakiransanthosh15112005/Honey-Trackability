package com.honeychain.admin.dto;

public class RegionalAnalyticsResponse {

    private String region;
    private long beekeepers;
    private long activeHives;
    private long batches;
    private double honeyProducedKg;
    private double averagePurity;
    private long products;

    public RegionalAnalyticsResponse() {
    }

    public RegionalAnalyticsResponse(String region, long beekeepers, long activeHives,
            long batches, double honeyProducedKg, double averagePurity, long products) {
        this.region = region;
        this.beekeepers = beekeepers;
        this.activeHives = activeHives;
        this.batches = batches;
        this.honeyProducedKg = honeyProducedKg;
        this.averagePurity = averagePurity;
        this.products = products;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public long getBeekeepers() {
        return beekeepers;
    }

    public void setBeekeepers(long beekeepers) {
        this.beekeepers = beekeepers;
    }

    public long getActiveHives() {
        return activeHives;
    }

    public void setActiveHives(long activeHives) {
        this.activeHives = activeHives;
    }

    public long getBatches() {
        return batches;
    }

    public void setBatches(long batches) {
        this.batches = batches;
    }

    public double getHoneyProducedKg() {
        return honeyProducedKg;
    }

    public void setHoneyProducedKg(double honeyProducedKg) {
        this.honeyProducedKg = honeyProducedKg;
    }

    public double getAveragePurity() {
        return averagePurity;
    }

    public void setAveragePurity(double averagePurity) {
        this.averagePurity = averagePurity;
    }

    public long getProducts() {
        return products;
    }

    public void setProducts(long products) {
        this.products = products;
    }
}
