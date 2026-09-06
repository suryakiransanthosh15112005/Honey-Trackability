package com.honeychain.lab.service;

import com.honeychain.lab.dto.LabTestCreateRequest;
import com.honeychain.lab.dto.LabTestResponse;
import com.honeychain.lab.dto.PendingBatchResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface LabTestService {

    List<PendingBatchResponse> getPendingBatches();

    LabTestResponse getLabTestByBatchId(String batchId);

    LabTestResponse getLabTestForBeekeeper(String phoneNumber, String batchId);

    LabTestResponse submitLabTest(String phoneNumber, String batchId, LabTestCreateRequest request, MultipartFile certificate);

    Map<String, Long> getLabStats();
}
