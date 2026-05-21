package com.example.creator_settlement.settlement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

// 전체 응답 DTO
@Getter
@AllArgsConstructor
public class AdminSettlementSummaryResponse {
    private Long totalPayout;
    private List<AdminSettlementResponse> settlements;
}
