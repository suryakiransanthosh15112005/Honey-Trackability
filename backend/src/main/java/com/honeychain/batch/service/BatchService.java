package com.honeychain.batch.service;

import java.util.List;
import java.util.Map;

public interface BatchService {

    Map<String, Object> getBatchDetails(Long batchId);

    List<Map<String, Object>> getBatchesByBeekeeper(String phoneNumber);
}
