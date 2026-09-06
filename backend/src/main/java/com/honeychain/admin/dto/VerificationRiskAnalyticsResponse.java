package com.honeychain.admin.dto;

import java.util.List;

public class VerificationRiskAnalyticsResponse {

    private long normalCount;
    private long watchCount;
    private long highRiskCount;
    private List<AdminBatchResponse> highRiskBatches;

    public VerificationRiskAnalyticsResponse() {
    }

    public VerificationRiskAnalyticsResponse(long normalCount, long watchCount, long highRiskCount,
            List<AdminBatchResponse> highRiskBatches) {
        this.normalCount = normalCount;
        this.watchCount = watchCount;
        this.highRiskCount = highRiskCount;
        this.highRiskBatches = highRiskBatches;
    }

    public long getNormalCount() {
        return normalCount;
    }

    public void setNormalCount(long normalCount) {
        this.normalCount = normalCount;
    }

    public long getWatchCount() {
        return watchCount;
    }

    public void setWatchCount(long watchCount) {
        this.watchCount = watchCount;
    }

    public long getHighRiskCount() {
        return highRiskCount;
    }

    public void setHighRiskCount(long highRiskCount) {
        this.highRiskCount = highRiskCount;
    }

    public List<AdminBatchResponse> getHighRiskBatches() {
        return highRiskBatches;
    }

    public void setHighRiskBatches(List<AdminBatchResponse> highRiskBatches) {
        this.highRiskBatches = highRiskBatches;
    }
}
