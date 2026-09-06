package com.honeychain;

import com.honeychain.admin.dto.DisputeResponse;
import com.honeychain.admin.dto.DisputeStatusUpdateRequest;
import com.honeychain.admin.entity.Dispute;
import com.honeychain.admin.entity.DisputeStatus;
import com.honeychain.admin.repository.DisputeRepository;
import com.honeychain.admin.service.impl.AdminDisputeServiceImpl;
import com.honeychain.common.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminDisputeServiceTest {

    @Mock
    private DisputeRepository disputeRepository;

    private AdminDisputeServiceImpl adminDisputeService;

    private Dispute dispute;

    @BeforeEach
    void setUp() {
        adminDisputeService = new AdminDisputeServiceImpl(disputeRepository);

        dispute = new Dispute("HC-2026-AB12CD34", "HC-ORD-2026-000001", 5L,
                "Customer suspects product authenticity", "Jar seal was broken on arrival");
        dispute.setId(100L);
    }

    @Test
    @DisplayName("Admin can transition OPEN dispute to INVESTIGATING")
    void testInvestigateDispute() {
        DisputeStatusUpdateRequest req = new DisputeStatusUpdateRequest(DisputeStatus.INVESTIGATING,
                "Assigning to regional KVIC inspector.");

        when(disputeRepository.findById(100L)).thenReturn(Optional.of(dispute));
        when(disputeRepository.save(any(Dispute.class))).thenAnswer(i -> i.getArgument(0));

        DisputeResponse res = adminDisputeService.updateDisputeStatus(100L, req);

        assertNotNull(res);
        assertEquals(DisputeStatus.INVESTIGATING, res.getStatus());
        assertEquals("Assigning to regional KVIC inspector.", res.getResolutionNotes());
    }

    @Test
    @DisplayName("Admin can transition dispute to RESOLVED")
    void testResolveDispute() {
        dispute.setStatus(DisputeStatus.INVESTIGATING);
        DisputeStatusUpdateRequest req = new DisputeStatusUpdateRequest(DisputeStatus.RESOLVED,
                "Inspected apiary, replacement dispatched.");

        when(disputeRepository.findById(100L)).thenReturn(Optional.of(dispute));
        when(disputeRepository.save(any(Dispute.class))).thenAnswer(i -> i.getArgument(0));

        DisputeResponse res = adminDisputeService.updateDisputeStatus(100L, req);

        assertNotNull(res);
        assertEquals(DisputeStatus.RESOLVED, res.getStatus());
    }

    @Test
    @DisplayName("Attempting to modify an already RESOLVED dispute throws BadRequestException")
    void testModifyResolvedDisputeThrowsException() {
        dispute.setStatus(DisputeStatus.RESOLVED);
        DisputeStatusUpdateRequest req = new DisputeStatusUpdateRequest(DisputeStatus.REJECTED,
                "Cannot alter resolved");

        when(disputeRepository.findById(100L)).thenReturn(Optional.of(dispute));

        assertThrows(BadRequestException.class, () -> adminDisputeService.updateDisputeStatus(100L, req));
    }
}
