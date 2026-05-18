package com.example.creator_settlement.cancellation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Getter
@Table(name = "cancel_record")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class CancelRecord {
    @Id
    private String id; // 취소된 강의 기록 ID

    @Column(nullable = false)
    private String saleRecordId; // 원본 판매 내역 참조를 위한 판매 기록 ID

    @Column(nullable = false)
    private Long refundAmount; // 환불 금액

    @Column(nullable = false)
    private OffsetDateTime canceledAt; // 취소 일시

    public CancelRecord(String id, String saleRecordId, Long refundAmount, OffsetDateTime canceledAt) {
        this.id = id;
        this.saleRecordId = saleRecordId;
        this.refundAmount = refundAmount;
        this.canceledAt = canceledAt;
    }
}
