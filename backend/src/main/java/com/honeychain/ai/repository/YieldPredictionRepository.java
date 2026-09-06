package com.honeychain.ai.repository;

import com.honeychain.ai.entity.YieldPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface YieldPredictionRepository extends JpaRepository<YieldPrediction, Long> {

    Optional<YieldPrediction> findFirstByHiveIdOrderByGeneratedAtDesc(Long hiveId);

    List<YieldPrediction> findAllByHiveBeekeeperProfileIdOrderByGeneratedAtDesc(Long beekeeperProfileId);

    List<YieldPrediction> findAllByHiveIdOrderByGeneratedAtDesc(Long hiveId);
}
