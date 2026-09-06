package com.honeychain.qr.service;

import com.honeychain.qr.dto.QrCodeResponse;

public interface QrCodeService {

    QrCodeResponse generateQrForBatch(String phoneNumber, String batchId);

    QrCodeResponse getQrForBatch(String phoneNumber, String batchId);

    QrCodeResponse getQrByBatchId(String batchId);
}
