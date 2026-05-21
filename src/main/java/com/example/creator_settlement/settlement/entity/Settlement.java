package com.example.creator_settlement.settlement.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity // 이 클래스는 DB 테이블로 사용
@Getter
@NoArgsConstructor // JPA가 DB 읽을 때 내부적으로 인자 없는 생성자 자동 생성
@AllArgsConstructor // 인자 있는 생성자 자동 생성
// 정산 결과를 기록하는 테이블
public class Settlement {
    @Id
    private String id;

    private String creatorId;

    private String month;

    private Long totalSalesAmount;

    private Long totalRefundAmount;

    private Long netSalesAmount;

    private Long platformFeeAmount;

    private Long payoutAmount;

    private int saleCount;

    private int cancelCount;

    @Enumerated(EnumType.STRING)
    private SettlementStatus status;

    private OffsetDateTime createdAt;

    private OffsetDateTime confirmedAt;

    private OffsetDateTime paidAt;

    // 객체 지향 캡슐화
    public void confirm() {
        this.status = SettlementStatus.CONFIRMED;
        this.confirmedAt = OffsetDateTime.now();
    }

    public void pay() {
        this.status = SettlementStatus.PAID;
        this.paidAt = OffsetDateTime.now();
    }
}
