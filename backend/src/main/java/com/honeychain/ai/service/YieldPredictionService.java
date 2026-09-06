package com.honeychain.ai.service;

import com.honeychain.ai.dto.YieldPredictionResponse;

import java.util.List;

public interface YieldPredictionService {

    YieldPredictionResponse getPredictionForHive(Long hiveId, String beekeeperPhone);

    List<YieldPredictionResponse> getAllPredictionsForBeekeeper(String beekeeperPhone);

    YieldPredictionResponse refreshPredictionForHive(Long hiveId, String beekeeperPhone);
}
