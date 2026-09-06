package com.honeychain.admin.service.impl;

import com.honeychain.admin.dto.DisputeResponse;
import com.honeychain.admin.dto.DisputeStatusUpdateRequest;
import com.honeychain.admin.entity.Dispute;
import com.honeychain.admin.entity.DisputeStatus;
import com.honeychain.admin.repository.DisputeRepository;
import com.honeychain.admin.service.AdminDisputeService;
import com.honeychain.common.dto.PageResponse;
import com.honeychain.common.exception.BadRequestException;
import com.honeychain.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminDisputeServiceImpl implements AdminDisputeService {

    private final DisputeRepository disputeRepository;

    public AdminDisputeServiceImpl(DisputeRepository disputeRepository) {
        this.disputeRepository = disputeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DisputeResponse> getDisputes(DisputeStatus status, Pageable pageable) {
        Page<Dispute> page = (status != null)
                ? disputeRepository.findAllByStatusOrderByCreatedAtDesc(status, pageable)
                : disputeRepository.findAllByOrderByCreatedAtDesc(pageable);
        List<DisputeResponse> content = page.getContent().stream().map(this::toResponse).toList();
        return PageResponse.of(page, content);
    }

    @Override
    @Transactional(readOnly = true)
    public DisputeResponse getDisputeDetails(Long id) {
        Dispute dispute = disputeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dispute not found with ID: " + id));
        return toResponse(dispute);
    }

    @Override
    public DisputeResponse updateDisputeStatus(Long id, DisputeStatusUpdateRequest request) {
        Dispute dispute = disputeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dispute not found with ID: " + id));

        DisputeStatus current = dispute.getStatus();
        DisputeStatus next = request.getStatus();

        if (current == DisputeStatus.RESOLVED || current == DisputeStatus.REJECTED) {
            throw new BadRequestException("Cannot change status of an already " + current + " dispute.");
        }

        dispute.setStatus(next);
        if (request.getResolutionNotes() != null && !request.getResolutionNotes().isBlank()) {
            dispute.setResolutionNotes(request.getResolutionNotes().trim());
        }

        Dispute saved = disputeRepository.save(dispute);
        return toResponse(saved);
    }

    private DisputeResponse toResponse(Dispute d) {
        return new DisputeResponse(
                d.getId(),
                d.getBatchId(),
                d.getOrderNumber(),
                d.getCustomerId(),
                d.getReason(),
                d.getDescription(),
                d.getStatus(),
                d.getResolutionNotes(),
                d.getCreatedAt(),
                d.getUpdatedAt());
    }
}
