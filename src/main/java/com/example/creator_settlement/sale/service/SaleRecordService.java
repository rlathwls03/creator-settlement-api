package com.example.creator_settlement.sale.service;

import com.example.creator_settlement.sale.dto.SaleRecordRequest;
import com.example.creator_settlement.sale.entity.SaleRecord;
import com.example.creator_settlement.sale.repository.SaleRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class SaleRecordService {
    private final SaleRecordRepository saleRecordRepository;

    public SaleRecord createSaleRecord(SaleRecordRequest request) {
        SaleRecord saleRecord = new SaleRecord (
            request.getId(),
            request.getCourseId(),
            request.getStudentId(),
            request.getAmount(),
            OffsetDateTime.parse(request.getPaidAt())
        );

        return saleRecordRepository.save(saleRecord);
    }
}
