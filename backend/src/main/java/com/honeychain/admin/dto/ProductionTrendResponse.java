package com.honeychain.admin.dto;

public class ProductionTrendResponse {

    private String month;
    private double quantityKg;
    private long batchCount;

    public ProductionTrendResponse() {
    }

    public ProductionTrendResponse(String month, double quantityKg, long batchCount) {
        this.month = month;
        this.quantityKg = quantityKg;
        this.batchCount = batchCount;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public double getQuantityKg() {
        return quantityKg;
    }

    public void setQuantityKg(double quantityKg) {
        this.quantityKg = quantityKg;
    }

    public long getBatchCount() {
        return batchCount;
    }

    public void setBatchCount(long batchCount) {
        this.batchCount = batchCount;
    }
}
