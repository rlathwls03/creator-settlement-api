package com.example.creator_settlement.sale.controller;

import com.example.creator_settlement.sale.dto.SaleRecordRequest;
import com.example.creator_settlement.sale.entity.SaleRecord;
import com.example.creator_settlement.sale.service.SaleRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SaleRecordController {
    private final SaleRecordService saleRecordService;

    @PostMapping("/api/sales")
    public SaleRecord createSaleRecord(@RequestBody SaleRecordRequest request) {
        return saleRecordService.createSaleRecord(request);
    }
}
