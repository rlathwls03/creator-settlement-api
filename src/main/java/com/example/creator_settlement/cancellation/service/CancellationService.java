package com.example.creator_settlement.cancellation.service;

import com.example.creator_settlement.cancellation.dto.CancelRequest;
import com.example.creator_settlement.cancellation.dto.CancelResponse;
import com.example.creator_settlement.cancellation.entity.CancelRecord;
import com.example.creator_settlement.cancellation.repository.CancelRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CancellationService {
    private final CancelRecordRepository cancelRecordRepository;

    public CancelResponse createCancelRecord(CancelRequest request) {
        CancelRecord cancelRecord = new CancelRecord(
                request.getId(),
                request.getSaleRecordId(),
                request.getRefundAmount(),
                request.getCanceledAt().toLocalDateTime()
        );

        CancelRecord savedCancelRecord = cancelRecordRepository.save(cancelRecord);

        return new CancelResponse(
                savedCancelRecord.getId(),
                savedCancelRecord.getSaleRecordId(),
                savedCancelRecord.getRefundAmount(),
                savedCancelRecord.getCanceledAt()
        );
    }
}
