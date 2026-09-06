package com.honeychain.iot.mapper;

import com.honeychain.hive.entity.Hive;
import com.honeychain.iot.dto.SensorDataResponse;
import com.honeychain.iot.entity.HiveSensorData;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class HiveSensorDataMapper {

    public SensorDataResponse toResponse(HiveSensorData data, Hive hive) {
        if (data == null) return null;
        return new SensorDataResponse(
                data.getHiveId(),
                hive != null ? hive.getHiveCode() : null,
                data.getTemperature(),
                data.getHumidity(),
                data.getBeeActivity(),
                data.getRecordedAt()
        );
    }

    public List<SensorDataResponse> toResponseList(List<HiveSensorData> dataList, Hive hive) {
        if (dataList == null) return List.of();
        return dataList.stream()
                .map(d -> toResponse(d, hive))
                .collect(Collectors.toList());
    }
}
