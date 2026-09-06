package com.honeychain.ai.mapper;

import com.honeychain.ai.dto.YieldPredictionExplanation;
import com.honeychain.ai.dto.YieldPredictionResponse;
import com.honeychain.ai.entity.YieldPrediction;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class YieldPredictionMapper {

    public YieldPredictionResponse toResponse(YieldPrediction entity, YieldPredictionExplanation details) {
        if (entity == null) {
            return null;
        }

        long daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), entity.getPredictedHarvestDate());
        if (daysUntil < 0) {
            daysUntil = 0;
        }

        String message = String.format("Expected harvest in about %d days (~%s-%s kg).",
                daysUntil,
                entity.getMinimumKg().toPlainString(),
                entity.getMaximumKg().toPlainString());

        return new YieldPredictionResponse(
                entity.getHive().getId(),
                entity.getHive().getHiveCode(),
                entity.getPredictedHarvestDate(),
                daysUntil,
                entity.getMinimumKg(),
                entity.getMaximumKg(),
                entity.getConfidence(),
                entity.getHealthStatus().name(),
                message,
                entity.getExplanation(),
                entity.getGeneratedAt(),
                details);
    }
}
