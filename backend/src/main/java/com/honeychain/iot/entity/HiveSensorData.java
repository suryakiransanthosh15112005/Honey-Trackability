package com.honeychain.iot.entity;

import com.honeychain.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Stores a single IoT sensor reading for a hive.
 * References the hive by ID only — does not embed hive business data.
 * Raw sensor data is neutral; health interpretation is done by HiveHealthService.
 */
@Entity
@Table(name = "hive_sensor_data",
        indexes = {
                @Index(name = "idx_sensor_hive_id", columnList = "hive_id"),
                @Index(name = "idx_sensor_recorded_at", columnList = "recorded_at"),
                @Index(name = "idx_sensor_hive_recorded", columnList = "hive_id, recorded_at")
        }
)
public class HiveSensorData extends BaseEntity {

    @Column(name = "hive_id", nullable = false)
    private Long hiveId;

    /** Temperature in degrees Celsius. Range for healthy hive: 32–37°C */
    @Column(name = "temperature", nullable = false, precision = 5, scale = 2)
    private BigDecimal temperature;

    /** Relative humidity percentage. Range for healthy hive: 45–70% */
    @Column(name = "humidity", nullable = false, precision = 5, scale = 2)
    private BigDecimal humidity;

    /** Bee activity score 0–100 (inferred from vibration/acoustic proxy in simulation). Healthy: >=70 */
    @Column(name = "bee_activity", nullable = false)
    private Integer beeActivity;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    public HiveSensorData() {
    }

    public HiveSensorData(Long hiveId, BigDecimal temperature, BigDecimal humidity,
                          Integer beeActivity, LocalDateTime recordedAt) {
        this.hiveId = hiveId;
        this.temperature = temperature;
        this.humidity = humidity;
        this.beeActivity = beeActivity;
        this.recordedAt = recordedAt != null ? recordedAt : LocalDateTime.now();
    }

    public Long getHiveId() { return hiveId; }
    public void setHiveId(Long hiveId) { this.hiveId = hiveId; }
    public BigDecimal getTemperature() { return temperature; }
    public void setTemperature(BigDecimal temperature) { this.temperature = temperature; }
    public BigDecimal getHumidity() { return humidity; }
    public void setHumidity(BigDecimal humidity) { this.humidity = humidity; }
    public Integer getBeeActivity() { return beeActivity; }
    public void setBeeActivity(Integer beeActivity) { this.beeActivity = beeActivity; }
    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }
}
