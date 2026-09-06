package com.honeychain.qr.mapper;

import com.honeychain.qr.dto.QrCodeResponse;
import com.honeychain.qr.entity.QrCode;
import org.springframework.stereotype.Component;

@Component
public class QrCodeMapper {

    public QrCodeResponse toResponse(QrCode qrCode) {
        if (qrCode == null)
            return null;

        return new QrCodeResponse(
                qrCode.getBatchId(),
                qrCode.getQrValue(),
                qrCode.getQrImageUrl(),
                qrCode.getGeneratedAt());
    }
}
