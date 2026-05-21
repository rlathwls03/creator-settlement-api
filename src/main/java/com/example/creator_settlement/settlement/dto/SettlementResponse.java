package com.example.creator_settlement.settlement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class SettlementResponse {
    private String creatorId;
    private String month;

    private Long totalSalesAmount; // 전체 판매 금액
    private Long totalRefundAmount; // 전체 환불 금액
    private Long netSalesAmount; // 순 판매 금액 = 전체 판매 금액 - 전체 환불 금액

    private Long platformFeeAmount; // 플랫폼 수수료 (초기에 20%로 고정)
    private Long payoutAmount; // 정산 금액 = 순 판매 금액 - 플랫폼 수수료

    private int saleCount; // 판매 건 수
    private int cancelCount; // 취소 건 수
}
