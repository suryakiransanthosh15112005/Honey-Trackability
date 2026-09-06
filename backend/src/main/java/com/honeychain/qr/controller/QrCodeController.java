package com.honeychain.qr.controller;

import com.honeychain.common.dto.ApiResponse;
import com.honeychain.qr.dto.QrCodeResponse;
import com.honeychain.qr.service.QrCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/beekeepers/batches/{batchId}")
@Tag(name = "QR Code Generation", description = "Endpoints for generating and retrieving scannable verification QR codes for honey batches")
@SecurityRequirement(name = "Bearer Authentication")
public class QrCodeController {

    private final QrCodeService qrCodeService;

    public QrCodeController(QrCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    @PostMapping("/generate-qr")
    @Operation(summary = "Generate QR Code", description = "Generates a scannable PNG QR code pointing to the public verification URL for a purity-verified batch")
    public ResponseEntity<ApiResponse<QrCodeResponse>> generateQr(
            Authentication authentication,
            @PathVariable String batchId) {
        QrCodeResponse response = qrCodeService.generateQrForBatch(authentication.getName(), batchId);
        return new ResponseEntity<>(ApiResponse.success("QR code generated successfully", response),
                HttpStatus.CREATED);
    }

    @GetMapping("/qr")
    @Operation(summary = "Get Batch QR Code", description = "Retrieves the existing QR code details for a honey batch")
    public ResponseEntity<ApiResponse<QrCodeResponse>> getQr(
            Authentication authentication,
            @PathVariable String batchId) {
        QrCodeResponse response = qrCodeService.getQrForBatch(authentication.getName(), batchId);
        return ResponseEntity.ok(ApiResponse.success("QR code retrieved successfully", response));
    }
}
