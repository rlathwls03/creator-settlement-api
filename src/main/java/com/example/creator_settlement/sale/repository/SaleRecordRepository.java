package com.example.creator_settlement.sale.repository;

import com.example.creator_settlement.sale.entity.SaleRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

// 정산 계산에 필요한 판매 내역을 조회하는 Repository
public interface SaleRecordRepository extends JpaRepository<SaleRecord, String> {
    // 특정기간의 판매 내역 조회 (기간 전체 판매 조회)
    List<SaleRecord> findByPaidAtBetween(OffsetDateTime startDate, OffsetDateTime endDate);

    List<SaleRecord> findByCourseIdIn(List<String> courseIds);

    // 특정 강의들 + 특정 기간의 판매 조회 (특정 creator의 월별 판매 조회)
    List<SaleRecord> findByCourseIdInAndPaidAtBetween(List<String> courseIds, OffsetDateTime startDate, OffsetDateTime endDate);
}
