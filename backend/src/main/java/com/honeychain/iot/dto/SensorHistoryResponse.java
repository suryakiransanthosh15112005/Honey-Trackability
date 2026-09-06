package com.honeychain.iot.dto;

import java.util.List;

public class SensorHistoryResponse {

    private Long hiveId;
    private String hiveCode;
    private int totalReadings;
    private List<SensorDataResponse> readings;

    public SensorHistoryResponse() {
    }

    public SensorHistoryResponse(Long hiveId, String hiveCode, int totalReadings, List<SensorDataResponse> readings) {
        this.hiveId = hiveId;
        this.hiveCode = hiveCode;
        this.totalReadings = totalReadings;
        this.readings = readings;
    }

    public Long getHiveId() {
        return hiveId;
    }

    public void setHiveId(Long hiveId) {
        this.hiveId = hiveId;
    }

    public String getHiveCode() {
        return hiveCode;
    }

    public void setHiveCode(String hiveCode) {
        this.hiveCode = hiveCode;
    }

    public int getTotalReadings() {
        return totalReadings;
    }

    public void setTotalReadings(int totalReadings) {
        this.totalReadings = totalReadings;
    }

    public List<SensorDataResponse> getReadings() {
        return readings;
    }

    public void setReadings(List<SensorDataResponse> readings) {
        this.readings = readings;
    }
}
