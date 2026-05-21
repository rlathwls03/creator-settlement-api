package com.example.creator_settlement.cancellation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
public class CancelResponse {
    private String id;
    private String saleRecordId;
    private Long refundAmount;
    private OffsetDateTime canceledAt;
}
