package com.example.creator_settlement.sale.controller;

import com.example.creator_settlement.sale.dto.SaleResponse;
import com.example.creator_settlement.sale.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SaleController {
    private final SaleService saleService;

    @GetMapping("/api/sales")
    public List<SaleResponse> getSales(
            @RequestParam String creatorId,
            @RequestParam OffsetDateTime startDate,
            @RequestParam OffsetDateTime endDate
    ) {
        return saleService.getSales(creatorId, startDate, endDate);
    }
}
