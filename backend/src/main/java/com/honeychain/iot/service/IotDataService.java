package com.honeychain.iot.service;

import java.util.Map;

public interface IotDataService {

    void ingestTelemetry(String deviceId, Map<String, Object> metrics);

    Map<String, Object> getLatestMetrics(String hiveId);
}
