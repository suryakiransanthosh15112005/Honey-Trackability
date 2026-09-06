package com.honeychain.qr.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.honeychain.batch.entity.BatchStatus;
import com.honeychain.batch.entity.HoneyBatch;
import com.honeychain.batch.repository.HoneyBatchRepository;
import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.beekeeper.repository.BeekeeperProfileRepository;
import com.honeychain.common.exception.BadRequestException;
import com.honeychain.common.exception.ResourceNotFoundException;
import com.honeychain.qr.dto.QrCodeResponse;
import com.honeychain.qr.entity.QrCode;
import com.honeychain.qr.mapper.QrCodeMapper;
import com.honeychain.qr.repository.QrCodeRepository;
import com.honeychain.qr.service.QrCodeService;
import com.honeychain.user.entity.User;
import com.honeychain.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class QrCodeServiceImpl implements QrCodeService {

    private static final Logger logger = LoggerFactory.getLogger(QrCodeServiceImpl.class);

    private final QrCodeRepository qrCodeRepository;
    private final HoneyBatchRepository honeyBatchRepository;
    private final BeekeeperProfileRepository beekeeperProfileRepository;
    private final UserService userService;
    private final QrCodeMapper qrCodeMapper;

    @Value("${app.frontend.public-url:http://localhost:5173}")
    private String frontendPublicUrl;

    @Value("${app.upload.base-dir:uploads}")
    private String baseUploadDir;

    public QrCodeServiceImpl(QrCodeRepository qrCodeRepository,
            HoneyBatchRepository honeyBatchRepository,
            BeekeeperProfileRepository beekeeperProfileRepository,
            UserService userService,
            QrCodeMapper qrCodeMapper) {
        this.qrCodeRepository = qrCodeRepository;
        this.honeyBatchRepository = honeyBatchRepository;
        this.beekeeperProfileRepository = beekeeperProfileRepository;
        this.userService = userService;
        this.qrCodeMapper = qrCodeMapper;
    }

    @Override
    @Transactional
    public QrCodeResponse generateQrForBatch(String phoneNumber, String batchId) {
        User user = userService.findEntityByPhoneNumber(phoneNumber);
        BeekeeperProfile profile = beekeeperProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BadRequestException("Beekeeper profile not found"));

        HoneyBatch batch = honeyBatchRepository.findByBatchIdAndBeekeeperProfileId(batchId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("HoneyBatch", "batchId", batchId));

        // Rule 1: If QR already exists, return existing QR code (idempotent)
        Optional<QrCode> existingQr = qrCodeRepository.findByBatchId(batchId);
        if (existingQr.isPresent()) {
            logger.info("QR code already exists for batch {}, returning existing record", batchId);
            return qrCodeMapper.toResponse(existingQr.get());
        }

        // Rule 2: Only PURE batches can generate QR
        if (batch.getStatus() != BatchStatus.PURE && batch.getStatus() != BatchStatus.QR_GENERATED) {
            throw new BadRequestException("QR code can only be generated for a purity-approved batch (Current status: "
                    + batch.getStatus() + ")");
        }

        // Construct public verification URL
        String cleanBaseUrl = frontendPublicUrl.endsWith("/")
                ? frontendPublicUrl.substring(0, frontendPublicUrl.length() - 1)
                : frontendPublicUrl;
        String publicVerificationUrl = cleanBaseUrl + "/verify/" + batchId;

        // Generate Real Scannable 500x500 PNG QR Code using ZXing
        String qrImageUrl = generateAndSaveQrImage(batchId, publicVerificationUrl);

        // Persist QR entity
        LocalDateTime generatedAt = LocalDateTime.now();
        QrCode qrCode = new QrCode(batchId, publicVerificationUrl, qrImageUrl, generatedAt);
        QrCode savedQr = qrCodeRepository.save(qrCode);

        // Update batch status to QR_GENERATED
        batch.setStatus(BatchStatus.QR_GENERATED);
        honeyBatchRepository.save(batch);

        logger.info("Generated QR code for batch {}: URL={}, ImagePath={}", batchId, publicVerificationUrl, qrImageUrl);

        return qrCodeMapper.toResponse(savedQr);
    }

    @Override
    @Transactional(readOnly = true)
    public QrCodeResponse getQrForBatch(String phoneNumber, String batchId) {
        User user = userService.findEntityByPhoneNumber(phoneNumber);
        BeekeeperProfile profile = beekeeperProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BadRequestException("Beekeeper profile not found"));

        honeyBatchRepository.findByBatchIdAndBeekeeperProfileId(batchId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("HoneyBatch", "batchId", batchId));

        QrCode qrCode = qrCodeRepository.findByBatchId(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("QrCode for batch", "batchId", batchId));

        return qrCodeMapper.toResponse(qrCode);
    }

    @Override
    @Transactional(readOnly = true)
    public QrCodeResponse getQrByBatchId(String batchId) {
        return qrCodeRepository.findByBatchId(batchId)
                .map(qrCodeMapper::toResponse)
                .orElse(null);
    }

    private String generateAndSaveQrImage(String batchId, String content) {
        try {
            Path targetDir = Paths.get(baseUploadDir, "qr").toAbsolutePath().normalize();
            Files.createDirectories(targetDir);

            String filename = batchId + ".png";
            Path targetPath = targetDir.resolve(filename);

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 2);

            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 500, 500, hints);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", targetPath);

            return "/uploads/qr/" + filename;
        } catch (IOException | com.google.zxing.WriterException e) {
            logger.error("Failed to generate QR code image for batch {}", batchId, e);
            throw new BadRequestException("Could not generate QR code image: " + e.getMessage());
        }
    }
}
