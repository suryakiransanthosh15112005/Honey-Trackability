package com.honeychain.batch.repository;

import com.honeychain.batch.entity.BatchCreationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BatchCreationRequestRepository extends JpaRepository<BatchCreationRequest, Long> {

        /**
         * Find an existing creation record by idempotency key scoped to the
         * authenticated beekeeper. The beekeeperProfileId scope prevents one
         * beekeeper from using another beekeeper's idempotency key to retrieve
         * their batch data.
         */
        Optional<BatchCreationRequest> findByIdempotencyKeyAndBeekeeperProfileId(
                        String idempotencyKey, Long beekeeperProfileId);

        boolean existsByIdempotencyKeyAndBeekeeperProfileId(
                        String idempotencyKey, Long beekeeperProfileId);
}
