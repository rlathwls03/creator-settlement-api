package com.example.creator_settlement.cancellation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
public class CancelRequest {
    private String id;
    private String saleRecordId;
    private Long refundAmount;
    private OffsetDateTime canceledAt;
}
