package com.example.creator_settlement.sale.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

// 판매 내역 조회 결과를 API 응답으로 보내기 위한 객체
@Getter
@AllArgsConstructor
public class SaleResponse {
    private String id;
    private String courseId;
    private String studentId;
    private Long amount;
    private OffsetDateTime paidAt;
}
