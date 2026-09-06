package com.honeychain.admin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.honeychain.lab.entity.LabTestResult;

import java.time.LocalDateTime;

public class AdminLabSummaryResponse {

    private Long id;
    private String batchId;
    private String beekeeperName;
    private String labName;
    private Integer purityScore;
    private LabTestResult result;
    private Double moisturePercentage;
    private Double sucrosePercentage;
    private Integer pollenCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime testDate;

    public AdminLabSummaryResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getBeekeeperName() {
        return beekeeperName;
    }

    public void setBeekeeperName(String beekeeperName) {
        this.beekeeperName = beekeeperName;
    }

    public String getLabName() {
        return labName;
    }

    public void setLabName(String labName) {
        this.labName = labName;
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

    public Double getMoisturePercentage() {
        return moisturePercentage;
    }

    public void setMoisturePercentage(Double moisturePercentage) {
        this.moisturePercentage = moisturePercentage;
    }

    public Double getSucrosePercentage() {
        return sucrosePercentage;
    }

    public void setSucrosePercentage(Double sucrosePercentage) {
        this.sucrosePercentage = sucrosePercentage;
    }

    public Integer getPollenCount() {
        return pollenCount;
    }

    public void setPollenCount(Integer pollenCount) {
        this.pollenCount = pollenCount;
    }

    public LocalDateTime getTestDate() {
        return testDate;
    }

    public void setTestDate(LocalDateTime testDate) {
        this.testDate = testDate;
    }
}
