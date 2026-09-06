package com.honeychain.verification.mapper;

import com.honeychain.verification.dto.VerificationEventResponse;
import com.honeychain.verification.dto.VerificationHistoryResponse;
import com.honeychain.verification.dto.VerificationRiskResponse;
import com.honeychain.verification.entity.VerificationHistory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class VerificationHistoryMapper {

        public VerificationEventResponse toEventResponse(VerificationHistory history) {
                if (history == null)
                        return null;
                return new VerificationEventResponse(
                                history.getResult() != null ? history.getResult().name() : "VERIFIED",
                                history.getScannedAt());
        }

        public VerificationHistoryResponse toHistoryResponse(long totalCount,
                        VerificationRiskResponse risk,
                        LocalDateTime lastVerifiedAt,
                        List<VerificationHistory> recentEvents) {
                List<VerificationEventResponse> eventResponses = recentEvents != null
                                ? recentEvents.stream().map(this::toEventResponse).collect(Collectors.toList())
                                : List.of();

                return new VerificationHistoryResponse(
                                totalCount,
                                risk != null ? risk.getRiskLevel() : null,
                                risk != null ? risk.getMessage() : null,
                                lastVerifiedAt,
                                eventResponses);
        }
}
