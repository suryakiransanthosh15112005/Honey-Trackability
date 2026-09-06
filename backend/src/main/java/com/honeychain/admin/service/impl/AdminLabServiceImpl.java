package com.honeychain.admin.service.impl;

import com.honeychain.admin.dto.AdminLabSummaryResponse;
import com.honeychain.admin.service.AdminLabService;
import com.honeychain.batch.repository.HoneyBatchRepository;
import com.honeychain.beekeeper.repository.BeekeeperProfileRepository;
import com.honeychain.common.dto.PageResponse;
import com.honeychain.lab.entity.LabTest;
import com.honeychain.lab.entity.LabTestResult;
import com.honeychain.lab.repository.LabTestRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AdminLabServiceImpl implements AdminLabService {

    private final LabTestRepository labTestRepository;
    private final HoneyBatchRepository honeyBatchRepository;
    private final BeekeeperProfileRepository beekeeperProfileRepository;

    public AdminLabServiceImpl(LabTestRepository labTestRepository,
            HoneyBatchRepository honeyBatchRepository,
            BeekeeperProfileRepository beekeeperProfileRepository) {
        this.labTestRepository = labTestRepository;
        this.honeyBatchRepository = honeyBatchRepository;
        this.beekeeperProfileRepository = beekeeperProfileRepository;
    }

    @Override
    public PageResponse<AdminLabSummaryResponse> getLabTests(LabTestResult result, Pageable pageable) {
        Page<LabTest> page = labTestRepository.findAdminLabTests(result, pageable);
        List<AdminLabSummaryResponse> content = page.getContent().stream().map(this::toResponse).toList();
        return PageResponse.of(page, content);
    }

    private AdminLabSummaryResponse toResponse(LabTest lab) {
        AdminLabSummaryResponse res = new AdminLabSummaryResponse();
        res.setId(lab.getId());
        res.setBatchId(lab.getBatchId());
        res.setLabName("NABL Certified Honey Testing Facility");
        res.setPurityScore(lab.getPurityScore());
        res.setResult(lab.getResult());
        res.setTestDate(lab.getTestedAt() != null ? lab.getTestedAt() : lab.getCreatedAt());

        honeyBatchRepository.findByBatchId(lab.getBatchId()).ifPresent(b -> {
            beekeeperProfileRepository.findById(b.getBeekeeperProfileId())
                    .ifPresent(bp -> res.setBeekeeperName(bp.getName()));
        });

        return res;
    }
}
