package com.example.creator_settlement.sale.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SaleResponse {
    private String id;
    private String courseId;
    private String studentId;
    private Long amount;
    private LocalDateTime paidAt;
}
