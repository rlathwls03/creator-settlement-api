package com.example.creator_settlement.settlement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 운영자용 정산 내역 집계 API
@Getter
@AllArgsConstructor
public class AdminSettlementResponse {
    private String creatorId; // 크리에이터 ID
    private Long payoutAmount; // 정산 금액
}
