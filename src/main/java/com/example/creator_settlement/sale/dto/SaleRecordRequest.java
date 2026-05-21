package com.example.creator_settlement.sale.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
// 판매 내역 등록 API
public class SaleRecordRequest {
    private String id;
    private String courseId;
    private String studentId;
    private Long amount;
    private String paidAt;
}
