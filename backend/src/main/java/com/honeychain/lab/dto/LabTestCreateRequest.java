package com.honeychain.lab.dto;

import com.honeychain.lab.entity.LabTestResult;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class LabTestCreateRequest {

    @NotNull(message = "Purity score is required")
    @Min(value = 0, message = "Purity score must be at least 0")
    @Max(value = 100, message = "Purity score cannot exceed 100")
    private Integer purityScore;

    @NotNull(message = "Lab test result is required")
    private LabTestResult result;

    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    private String remarks;

    public LabTestCreateRequest() {
    }

    public LabTestCreateRequest(Integer purityScore, LabTestResult result, String remarks) {
        this.purityScore = purityScore;
        this.result = result;
        this.remarks = remarks;
    }

    public Integer getPurityScore() {
        return purityScore;
    }

    public void setPurityScore(Integer purityScore) {
        this.purityScore = purityScore;
    }

    public LabTestResult getResult() {
        return result;
    }

    public void setResult(LabTestResult result) {
        this.result = result;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
