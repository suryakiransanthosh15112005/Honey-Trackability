package com.honeychain.lab.entity;

import com.honeychain.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(name = "lab_tests", uniqueConstraints = {
        @UniqueConstraint(name = "uk_lab_tests_batch_id", columnNames = { "batch_id" })
}, indexes = {
        @Index(name = "idx_lab_tests_batch_id", columnList = "batch_id"),
        @Index(name = "idx_lab_tests_result", columnList = "result")
})
public class LabTest extends BaseEntity {

    @Column(name = "batch_id", nullable = false, unique = true, length = 30)
    private String batchId;

    @Column(name = "lab_user_id", nullable = false)
    private Long labUserId;

    @Column(name = "purity_score", nullable = false)
    private Integer purityScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", nullable = false, length = 30)
    private LabTestResult result;

    @Column(name = "certificate_url", length = 255)
    private String certificateUrl;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @Column(name = "tested_at", nullable = false)
    private LocalDateTime testedAt;

    public LabTest() {
    }

    public LabTest(String batchId, Long labUserId, Integer purityScore, LabTestResult result,
            String certificateUrl, String remarks, LocalDateTime testedAt) {
        this.batchId = batchId;
        this.labUserId = labUserId;
        this.purityScore = purityScore;
        this.result = result;
        this.certificateUrl = certificateUrl;
        this.remarks = remarks;
        this.testedAt = testedAt != null ? testedAt : LocalDateTime.now();
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public Long getLabUserId() {
        return labUserId;
    }

    public void setLabUserId(Long labUserId) {
        this.labUserId = labUserId;
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

    public String getCertificateUrl() {
        return certificateUrl;
    }

    public void setCertificateUrl(String certificateUrl) {
        this.certificateUrl = certificateUrl;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getTestedAt() {
        return testedAt;
    }

    public void setTestedAt(LocalDateTime testedAt) {
        this.testedAt = testedAt;
    }
}
