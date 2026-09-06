package com.honeychain.batch.controller;

import com.honeychain.batch.dto.HoneyBatchCreateRequest;
import com.honeychain.batch.dto.HoneyBatchResponse;
import com.honeychain.batch.dto.HoneyBatchUpdateRequest;
import com.honeychain.batch.service.HoneyBatchService;
import com.honeychain.common.dto.ApiResponse;
import com.honeychain.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/beekeepers/batches")
@Tag(name = "Honey Batch Management", description = "Endpoints for beekeepers to create and manage honey batches")
@SecurityRequirement(name = "Bearer Authentication")
public class HoneyBatchController {

    private final HoneyBatchService honeyBatchService;

    public HoneyBatchController(HoneyBatchService honeyBatchService) {
        this.honeyBatchService = honeyBatchService;
    }

    /**
     * Multipart batch creation. Accepts an optional X-Idempotency-Key header
     * for offline sync deduplication. When the same key is received twice for
     * the same beekeeper, the existing batch is returned (HTTP 200) instead of
     * creating a duplicate (HTTP 201).
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create Batch with Photo (Multipart)", description = "Create a new honey batch with optional photo upload. "
            + "Pass X-Idempotency-Key header for offline sync idempotency.")
    public ResponseEntity<ApiResponse<HoneyBatchResponse>> createBatchMultipart(
            Authentication authentication,
            @Valid @ModelAttribute HoneyBatchCreateRequest request,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey) {

        request.setIdempotencyKey(idempotencyKey);

        HoneyBatchResponse response = honeyBatchService.createBatch(
                authentication.getName(), request, photo, idempotencyKey);

        return new ResponseEntity<>(
                ApiResponse.success("Batch " + response.getBatchId() + " created successfully", response),
                HttpStatus.CREATED);
    }

    /**
     * JSON batch creation (no photo). Accepts an optional X-Idempotency-Key header.
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create Batch (JSON)", description = "Create a new honey batch without photo upload. "
            + "Pass X-Idempotency-Key header for offline sync idempotency.")
    public ResponseEntity<ApiResponse<HoneyBatchResponse>> createBatchJson(
            Authentication authentication,
            @Valid @RequestBody HoneyBatchCreateRequest request,
            @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey) {

        request.setIdempotencyKey(idempotencyKey);

        HoneyBatchResponse response = honeyBatchService.createBatch(
                authentication.getName(), request, null, idempotencyKey);

        return new ResponseEntity<>(
                ApiResponse.success("Batch " + response.getBatchId() + " created successfully", response),
                HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get My Batches", description = "Paginated list of batches belonging to the authenticated beekeeper")
    public ResponseEntity<ApiResponse<PageResponse<HoneyBatchResponse>>> getMyBatches(
            Authentication authentication,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "createdAt,desc") String sort) {
        PageResponse<HoneyBatchResponse> batches = honeyBatchService.getMyBatches(
                authentication.getName(), page, size, sort);
        return ResponseEntity.ok(ApiResponse.success("Batches retrieved successfully", batches));
    }

    @GetMapping("/{batchId}")
    @Operation(summary = "Get Batch Details", description = "Get details of a specific batch by Batch ID")
    public ResponseEntity<ApiResponse<HoneyBatchResponse>> getMyBatch(
            Authentication authentication,
            @PathVariable String batchId) {
        HoneyBatchResponse response = honeyBatchService.getMyBatch(authentication.getName(), batchId);
        return ResponseEntity.ok(ApiResponse.success("Batch details retrieved", response));
    }

    @PutMapping(value = "/{batchId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update Batch (Multipart)", description = "Update batch details with optional new photo (only if CREATED)")
    public ResponseEntity<ApiResponse<HoneyBatchResponse>> updateBatchMultipart(
            Authentication authentication,
            @PathVariable String batchId,
            @Valid @ModelAttribute HoneyBatchUpdateRequest request,
            @RequestParam(value = "photo", required = false) MultipartFile photo) {
        HoneyBatchResponse response = honeyBatchService.updateBatch(
                authentication.getName(), batchId, request, photo);
        return ResponseEntity.ok(ApiResponse.success("Batch updated successfully", response));
    }

    @PutMapping(value = "/{batchId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update Batch (JSON)", description = "Update batch details without changing photo (only if CREATED)")
    public ResponseEntity<ApiResponse<HoneyBatchResponse>> updateBatchJson(
            Authentication authentication,
            @PathVariable String batchId,
            @Valid @RequestBody HoneyBatchUpdateRequest request) {
        HoneyBatchResponse response = honeyBatchService.updateBatch(
                authentication.getName(), batchId, request, null);
        return ResponseEntity.ok(ApiResponse.success("Batch updated successfully", response));
    }

    @PostMapping("/{batchId}/send-testing")
    @Operation(summary = "Send for Testing", description = "Transition batch status from CREATED to SENT_FOR_TESTING")
    public ResponseEntity<ApiResponse<HoneyBatchResponse>> sendForTesting(
            Authentication authentication,
            @PathVariable String batchId) {
        HoneyBatchResponse response = honeyBatchService.sendForTesting(authentication.getName(), batchId);
        return ResponseEntity.ok(ApiResponse.success("Batch sent for testing", response));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get Batch Stats", description = "Retrieve count of batches by status for the current beekeeper")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getBatchStats(Authentication authentication) {
        Map<String, Long> stats = honeyBatchService.getBatchStats(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Batch statistics retrieved", stats));
    }
}