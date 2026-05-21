package com.example.creator_settlement.cancellation.controller;

import com.example.creator_settlement.cancellation.dto.CancelRequest;
import com.example.creator_settlement.cancellation.dto.CancelResponse;
import com.example.creator_settlement.cancellation.service.CancellationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor // final 븥은 변수 생성자 자동 생성
public class CancellationController {
    private final CancellationService cancellationService;

    @PostMapping("/api/cancellations")
    public CancelResponse createCancelRecord(@RequestBody CancelRequest request) {
        return cancellationService.createCancelRecord(request);
    }
}
