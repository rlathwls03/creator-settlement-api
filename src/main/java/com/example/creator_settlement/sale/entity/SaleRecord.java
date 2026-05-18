package com.example.creator_settlement.sale.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Getter
@Table(name = "sale_record")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class SaleRecord { // 강의 판매 기록
    @Id
    private String saleRecordId; // 판매 강의 기록 ID

    @Column(nullable = false)
    private String courseId; // 판매된 강의 ID

    @Column(nullable = false)
    private String studentId; // 강의를 듣는 수강생 ID

    @Column(nullable = false)
    private Long saleAmount; // 결제 금액

    @Column(nullable = false)
    private OffsetDateTime paidAt; // 결제 일시

    public SaleRecord(String saleRecordId, String courseId, String studentId, Long saleAmount, OffsetDateTime paidAt) {
        this.saleRecordId = saleRecordId;
        this.courseId = courseId;
        this.studentId = studentId;
        this.saleAmount = saleAmount;
        this.paidAt = paidAt;
    }
}
