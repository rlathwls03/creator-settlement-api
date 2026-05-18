package com.example.creator_settlement.cancellation.repository;

import com.example.creator_settlement.cancellation.entity.CancelRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

// 환불(취소) 내역을 조회하는 Repository
public interface CancelRecordRepository extends JpaRepository<CancelRecord, String> {
     // 특정 기간의 취소 내역 조회 (기간 전체 환불 조회)
     List<CancelRecord> findByCanceledAtBetween(OffsetDateTime startDate, OffsetDateTime endDate);
     // 특정 판매 기록들 + 특정 기간의 환불 조회 (특정 creator의 월별 환불 조회)
     List<CancelRecord> findBySaleRecordIdInAndCanceledAtBetween(List<String> ids, OffsetDateTime startDate, OffsetDateTime endDate);
}
