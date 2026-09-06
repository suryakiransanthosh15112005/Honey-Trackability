package com.honeychain.iot.repository;

import com.honeychain.iot.entity.HiveSensorData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HiveSensorDataRepository extends JpaRepository<HiveSensorData, Long> {

    Optional<HiveSensorData> findTop1ByHiveIdOrderByRecordedAtDesc(Long hiveId);

    List<HiveSensorData> findByHiveIdOrderByRecordedAtDesc(Long hiveId, Pageable pageable);

    List<HiveSensorData> findByHiveIdAndRecordedAtAfterOrderByRecordedAtDesc(Long hiveId, LocalDateTime after);

    long countByHiveId(Long hiveId);
}
