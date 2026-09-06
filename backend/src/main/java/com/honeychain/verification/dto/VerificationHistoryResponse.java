package com.honeychain.verification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.honeychain.verification.entity.VerificationRiskLevel;

import java.time.LocalDateTime;
import java.util.List;

public class VerificationHistoryResponse {

    private long totalVerifications;
    private VerificationRiskLevel riskLevel;
    private String riskMessage;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastVerifiedAt;

    private List<VerificationEventResponse> recentEvents;

    public VerificationHistoryResponse() {
    }

    public VerificationHistoryResponse(long totalVerifications, VerificationRiskLevel riskLevel, String riskMessage,
            LocalDateTime lastVerifiedAt, List<VerificationEventResponse> recentEvents) {
        this.totalVerifications = totalVerifications;
        this.riskLevel = riskLevel;
        this.riskMessage = riskMessage;
        this.lastVerifiedAt = lastVerifiedAt;
        this.recentEvents = recentEvents;
    }

    public long getTotalVerifications() {
        return totalVerifications;
    }

    public void setTotalVerifications(long totalVerifications) {
        this.totalVerifications = totalVerifications;
    }

    public VerificationRiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(VerificationRiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getRiskMessage() {
        return riskMessage;
    }

    public void setRiskMessage(String riskMessage) {
        this.riskMessage = riskMessage;
    }

    public LocalDateTime getLastVerifiedAt() {
        return lastVerifiedAt;
    }

    public void setLastVerifiedAt(LocalDateTime lastVerifiedAt) {
        this.lastVerifiedAt = lastVerifiedAt;
    }

    public List<VerificationEventResponse> getRecentEvents() {
        return recentEvents;
    }

    public void setRecentEvents(List<VerificationEventResponse> recentEvents) {
        this.recentEvents = recentEvents;
    }
}
