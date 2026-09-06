package com.honeychain.admin.service.impl;

import com.honeychain.admin.dto.CreateDisputeRequest;
import com.honeychain.admin.dto.DisputeResponse;
import com.honeychain.admin.entity.Dispute;
import com.honeychain.admin.entity.DisputeStatus;
import com.honeychain.admin.repository.DisputeRepository;
import com.honeychain.admin.service.CustomerDisputeService;
import com.honeychain.common.dto.PageResponse;
import com.honeychain.common.exception.ResourceNotFoundException;
import com.honeychain.user.entity.User;
import com.honeychain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CustomerDisputeServiceImpl implements CustomerDisputeService {

    private final DisputeRepository disputeRepository;
    private final UserRepository userRepository;

    public CustomerDisputeServiceImpl(DisputeRepository disputeRepository, UserRepository userRepository) {
        this.disputeRepository = disputeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public DisputeResponse createDispute(String customerPhoneNumber, CreateDisputeRequest request) {
        User customer = userRepository.findByPhoneNumber(customerPhoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Customer user not found: " + customerPhoneNumber));

        Dispute dispute = new Dispute(
                request.getBatchId().trim(),
                request.getOrderNumber() != null ? request.getOrderNumber().trim() : null,
                customer.getId(),
                request.getReason().trim(),
                request.getDescription() != null ? request.getDescription().trim() : null
        );
        dispute.setStatus(DisputeStatus.OPEN);

        Dispute saved = disputeRepository.save(dispute);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DisputeResponse> getMyDisputes(String customerPhoneNumber, Pageable pageable) {
        User customer = userRepository.findByPhoneNumber(customerPhoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Customer user not found: " + customerPhoneNumber));

        Page<Dispute> page = disputeRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getId(), pageable);
        List<DisputeResponse> content = page.getContent().stream().map(this::toResponse).toList();
        return PageResponse.of(page, content);
    }

    @Override
    @Transactional(readOnly = true)
    public DisputeResponse getDisputeById(String customerPhoneNumber, Long id) {
        User customer = userRepository.findByPhoneNumber(customerPhoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Customer user not found: " + customerPhoneNumber));

        Dispute dispute = disputeRepository.findByIdAndCustomerId(id, customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Dispute not found with ID: " + id));

        return toResponse(dispute);
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
                d.getUpdatedAt()
        );
    }
}
