package com.honeychain.verification.dto;

import com.honeychain.verification.entity.VerificationRiskLevel;

public class VerificationRiskResponse {

    private VerificationRiskLevel riskLevel;
    private String message;

    public VerificationRiskResponse() {
    }

    public VerificationRiskResponse(VerificationRiskLevel riskLevel, String message) {
        this.riskLevel = riskLevel;
        this.message = message;
    }

    public VerificationRiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(VerificationRiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
