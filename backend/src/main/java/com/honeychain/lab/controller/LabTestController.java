package com.honeychain.lab.controller;

import com.honeychain.common.dto.ApiResponse;
import com.honeychain.lab.dto.LabTestCreateRequest;
import com.honeychain.lab.dto.LabTestResponse;
import com.honeychain.lab.dto.PendingBatchResponse;
import com.honeychain.lab.service.LabTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Lab Testing & Purity Verification", description = "Endpoints for laboratory testing, purity certification, and beekeeper result queries")
@SecurityRequirement(name = "Bearer Authentication")
public class LabTestController {

    private final LabTestService labTestService;

    public LabTestController(LabTestService labTestService) {
        this.labTestService = labTestService;
    }

    @GetMapping("/api/lab/tests/pending")
    @Operation(summary = "Get Pending Batches", description = "List all honey batches awaiting laboratory analysis")
    public ResponseEntity<ApiResponse<List<PendingBatchResponse>>> getPendingBatches() {
        List<PendingBatchResponse> pending = labTestService.getPendingBatches();
        return ResponseEntity.ok(ApiResponse.success("Pending batches retrieved", pending));
    }

    @GetMapping("/api/lab/tests/{batchId}")
    @Operation(summary = "Get Lab Test by Batch ID", description = "Retrieve test details and existing results for a specific batch")
    public ResponseEntity<ApiResponse<LabTestResponse>> getLabTestByBatchId(@PathVariable String batchId) {
        LabTestResponse response = labTestService.getLabTestByBatchId(batchId);
        return ResponseEntity.ok(ApiResponse.success("Lab test details retrieved", response));
    }

    @PostMapping(value = "/api/lab/tests/{batchId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Submit Lab Test (Multipart)", description = "Submit lab test results, purity score, and optional certificate document")
    public ResponseEntity<ApiResponse<LabTestResponse>> submitLabTestMultipart(
            Authentication authentication,
            @PathVariable String batchId,
            @Valid @ModelAttribute LabTestCreateRequest request,
            @RequestParam(value = "certificate", required = false) MultipartFile certificate) {
        LabTestResponse response = labTestService.submitLabTest(authentication.getName(), batchId, request,
                certificate);
        return new ResponseEntity<>(ApiResponse.success("Lab test submitted successfully", response),
                HttpStatus.CREATED);
    }

    @PostMapping(value = "/api/lab/tests/{batchId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Submit Lab Test (JSON)", description = "Submit lab test results and purity score without certificate upload")
    public ResponseEntity<ApiResponse<LabTestResponse>> submitLabTestJson(
            Authentication authentication,
            @PathVariable String batchId,
            @Valid @RequestBody LabTestCreateRequest request) {
        LabTestResponse response = labTestService.submitLabTest(authentication.getName(), batchId, request, null);
        return new ResponseEntity<>(ApiResponse.success("Lab test submitted successfully", response),
                HttpStatus.CREATED);
    }

    @GetMapping("/api/lab/stats")
    @Operation(summary = "Get Lab Statistics", description = "Retrieve summary metrics of pending and completed tests")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getLabStats() {
        Map<String, Long> stats = labTestService.getLabStats();
        return ResponseEntity.ok(ApiResponse.success("Lab statistics retrieved", stats));
    }

    @GetMapping("/api/beekeepers/batches/{batchId}/lab-result")
    @Operation(summary = "Get Beekeeper Lab Result", description = "Beekeeper endpoint to retrieve purity test result for their owned batch")
    public ResponseEntity<ApiResponse<LabTestResponse>> getBeekeeperLabResult(
            Authentication authentication,
            @PathVariable String batchId) {
        LabTestResponse response = labTestService.getLabTestForBeekeeper(authentication.getName(), batchId);
        return ResponseEntity.ok(ApiResponse.success("Lab result retrieved", response));
    }
}
