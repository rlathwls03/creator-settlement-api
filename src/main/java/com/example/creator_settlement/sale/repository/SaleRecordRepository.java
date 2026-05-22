package com.example.creator_settlement.sale.repository;

import com.example.creator_settlement.sale.entity.SaleRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleRecordRepository extends JpaRepository<SaleRecord, String> {

    List<SaleRecord> findByCourseIdIn(List<String> courseIds);

    List<SaleRecord> findByCourseIdInAndPaidAtGreaterThanEqualAndPaidAtLessThan(
            List<String> courseIds, LocalDateTime startDate, LocalDateTime endDate);
}
