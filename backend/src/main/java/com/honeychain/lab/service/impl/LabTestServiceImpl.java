package com.honeychain.lab.service.impl;

import com.honeychain.batch.entity.BatchStatus;
import com.honeychain.batch.entity.HoneyBatch;
import com.honeychain.batch.repository.HoneyBatchRepository;
import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.beekeeper.repository.BeekeeperProfileRepository;
import com.honeychain.blockchain.dto.BlockchainRecordResponse;
import com.honeychain.blockchain.entity.BlockchainRecordType;
import com.honeychain.blockchain.service.BlockchainService;
import com.honeychain.common.exception.BadRequestException;
import com.honeychain.common.exception.ConflictException;
import com.honeychain.common.exception.ResourceNotFoundException;
import com.honeychain.common.service.StorageService;
import com.honeychain.hive.entity.Hive;
import com.honeychain.hive.repository.HiveRepository;
import com.honeychain.lab.dto.LabTestCreateRequest;
import com.honeychain.lab.dto.LabTestResponse;
import com.honeychain.lab.dto.PendingBatchResponse;
import com.honeychain.lab.entity.LabTest;
import com.honeychain.lab.entity.LabTestResult;
import com.honeychain.lab.mapper.LabTestMapper;
import com.honeychain.lab.repository.LabTestRepository;
import com.honeychain.lab.service.LabTestService;
import com.honeychain.notification.entity.NotificationType;
import com.honeychain.notification.event.NotificationEvent;
import com.honeychain.user.entity.User;
import com.honeychain.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LabTestServiceImpl implements LabTestService {

    private static final Logger logger = LoggerFactory.getLogger(LabTestServiceImpl.class);

    private final LabTestRepository labTestRepository;
    private final HoneyBatchRepository honeyBatchRepository;
    private final BeekeeperProfileRepository beekeeperProfileRepository;
    private final HiveRepository hiveRepository;
    private final UserService userService;
    private final StorageService storageService;
    private final BlockchainService blockchainService;
    private final LabTestMapper labTestMapper;
    private final ApplicationEventPublisher eventPublisher;

    public LabTestServiceImpl(LabTestRepository labTestRepository,
                               HoneyBatchRepository honeyBatchRepository,
                               BeekeeperProfileRepository beekeeperProfileRepository,
                               HiveRepository hiveRepository,
                               UserService userService,
                               StorageService storageService,
                               BlockchainService blockchainService,
                               LabTestMapper labTestMapper) {
        this(labTestRepository, honeyBatchRepository, beekeeperProfileRepository, hiveRepository, userService, storageService, blockchainService, labTestMapper, event -> {});
    }

    @org.springframework.beans.factory.annotation.Autowired
    public LabTestServiceImpl(LabTestRepository labTestRepository,
                              HoneyBatchRepository honeyBatchRepository,
                              BeekeeperProfileRepository beekeeperProfileRepository,
                              HiveRepository hiveRepository,
                              UserService userService,
                              StorageService storageService,
                              BlockchainService blockchainService,
                              LabTestMapper labTestMapper,
                              ApplicationEventPublisher eventPublisher) {
        this.labTestRepository = labTestRepository;
        this.honeyBatchRepository = honeyBatchRepository;
        this.beekeeperProfileRepository = beekeeperProfileRepository;
        this.hiveRepository = hiveRepository;
        this.userService = userService;
        this.storageService = storageService;
        this.blockchainService = blockchainService;
        this.labTestMapper = labTestMapper;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PendingBatchResponse> getPendingBatches() {
        List<HoneyBatch> pendingBatches = honeyBatchRepository.findAll().stream()
                .filter(b -> b.getStatus() == BatchStatus.SENT_FOR_TESTING)
                .collect(Collectors.toList());

        Map<Long, BeekeeperProfile> profileMap = beekeeperProfileRepository.findAll().stream()
                .collect(Collectors.toMap(BeekeeperProfile::getId, p -> p));

        Map<Long, Hive> hiveMap = hiveRepository.findAll().stream()
                .collect(Collectors.toMap(Hive::getId, h -> h));

        return pendingBatches.stream()
                .map(batch -> labTestMapper.toPendingResponse(
                        batch,
                        profileMap.get(batch.getBeekeeperProfileId()),
                        hiveMap.get(batch.getHiveId())
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LabTestResponse getLabTestByBatchId(String batchId) {
        LabTest test = labTestRepository.findByBatchId(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("LabTest", "batchId", batchId));

        BlockchainRecordResponse blockchainRecord = blockchainService.getBatchRecord(batchId, BlockchainRecordType.LAB_RESULT);
        return labTestMapper.toResponse(test, blockchainRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public LabTestResponse getLabTestForBeekeeper(String phoneNumber, String batchId) {
        User user = userService.findEntityByPhoneNumber(phoneNumber);
        BeekeeperProfile profile = beekeeperProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BadRequestException("Beekeeper profile not found"));

        // Verify batch ownership
        honeyBatchRepository.findByBatchIdAndBeekeeperProfileId(batchId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("HoneyBatch", "batchId", batchId));

        LabTest test = labTestRepository.findByBatchId(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("LabTest for batch", "batchId", batchId));

        BlockchainRecordResponse blockchainRecord = blockchainService.getBatchRecord(batchId, BlockchainRecordType.LAB_RESULT);
        return labTestMapper.toResponse(test, blockchainRecord);
    }

    @Override
    @Transactional
    public LabTestResponse submitLabTest(String phoneNumber, String batchId, LabTestCreateRequest request, MultipartFile certificate) {
        User labUser = userService.findEntityByPhoneNumber(phoneNumber);

        HoneyBatch batch = honeyBatchRepository.findByBatchId(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("HoneyBatch", "batchId", batchId));

        // Validation 1: Batch must be awaiting testing
        if (batch.getStatus() != BatchStatus.SENT_FOR_TESTING) {
            throw new BadRequestException("Batch " + batchId + " is not awaiting lab testing (Current status: " + batch.getStatus() + ")");
        }

        // Validation 2: Check duplicate test
        if (labTestRepository.existsByBatchId(batchId)) {
            throw new ConflictException("Lab result already exists for batch " + batchId);
        }

        // Upload certificate if provided
        String certificateUrl = null;
        if (certificate != null && !certificate.isEmpty()) {
            certificateUrl = storageService.storeFile(certificate, "lab-certificates");
        }

        LocalDateTime testedAt = LocalDateTime.now();
        LabTest labTest = labTestMapper.toEntity(batchId, labUser.getId(), request, certificateUrl, testedAt);
        LabTest savedTest = labTestRepository.save(labTest);

        // Update HoneyBatch status according to lab test result
        BatchStatus nextBatchStatus;
        if (request.getResult() == LabTestResult.PURE) {
            nextBatchStatus = BatchStatus.PURE;
        } else if (request.getResult() == LabTestResult.UNDER_REVIEW) {
            nextBatchStatus = BatchStatus.UNDER_REVIEW;
        } else {
            nextBatchStatus = BatchStatus.FAILED;
        }
        batch.setStatus(nextBatchStatus);
        honeyBatchRepository.save(batch);

        // Record immutable lab result on blockchain
        BlockchainRecordResponse blockchainRecord = blockchainService.recordLabResult(
                batchId,
                request.getPurityScore(),
                request.getResult().name(),
                testedAt
        );

        // Notify beekeeper of lab result availability
        BeekeeperProfile beekeeperProfile = beekeeperProfileRepository.findById(batch.getBeekeeperProfileId()).orElse(null);
        if (beekeeperProfile != null) {
            String title = "Lab Result Available";
            String msg = String.format("Your batch %s has been tested. Purity: %.1f%%. Result: %s.",
                    batchId, request.getPurityScore().doubleValue(), request.getResult());
            eventPublisher.publishEvent(new NotificationEvent(
                    beekeeperProfile.getUserId(),
                    title,
                    msg,
                    NotificationType.LAB_RESULT,
                    "BATCH",
                    batchId,
                    true
            ));
        }

        logger.info("Lab test submitted for batch {}: Result={}, Purity={}%, BlockchainTx={}",
                batchId, request.getResult(), request.getPurityScore(), blockchainRecord.getTransactionHash());

        return labTestMapper.toResponse(savedTest, blockchainRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getLabStats() {
        long pending = honeyBatchRepository.findAll().stream()
                .filter(b -> b.getStatus() == BatchStatus.SENT_FOR_TESTING)
                .count();

        long pure = labTestRepository.countByResult(LabTestResult.PURE);
        long underReview = labTestRepository.countByResult(LabTestResult.UNDER_REVIEW);
        long failed = labTestRepository.countByResult(LabTestResult.FAILED);
        long completed = pure + underReview + failed;

        return Map.of(
                "pending", pending,
                "completed", completed,
                "pure", pure,
                "underReview", underReview,
                "failed", failed
        );
    }
}
