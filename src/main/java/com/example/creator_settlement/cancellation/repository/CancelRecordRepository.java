package com.example.creator_settlement.cancellation.repository;

import com.example.creator_settlement.cancellation.entity.CancelRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CancelRecordRepository extends JpaRepository<CancelRecord, String> {

    List<CancelRecord> findBySaleRecordIdInAndCanceledAtGreaterThanEqualAndCanceledAtLessThan(
            List<String> ids, LocalDateTime startDate, LocalDateTime endDate);
}
