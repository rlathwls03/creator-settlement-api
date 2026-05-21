package com.example.creator_settlement.settlement.controller;

// 정산 결과를 담아서 응답으로 보낼 DTO
import com.example.creator_settlement.settlement.dto.AdminSettlementSummaryResponse;
import com.example.creator_settlement.settlement.dto.SettlementResponse;
// 실제 정산 계산 로직이 들어있는 Service
import com.example.creator_settlement.settlement.service.SettlementService;
// 생성자 자동 생성
import lombok.RequiredArgsConstructor;
// Spring에게 "이 클래스는 Controller야"라고 알려줌
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// URL 파라미터 받기 위한 어노테이션
// REST API Controller 선언

import java.time.OffsetDateTime;

// 사용자가 URL(API)로 요청 -> Service를 호출 -> 결과 반환

// 이 클래스는 REST API를 처리하는 Controller라고 Spring에게 알려줌
@RestController // JSON 반환, Controller는 HTML 화면 반환
// final 필드를 사용하는 생성자를 자동 생성
@RequiredArgsConstructor
public class SettlementController {
    // Service 객체 주입
    // Controller는 계산 안 하고 Service에게 맡김
    private final SettlementService settlementService;

    // GET 요청이 오면 아래 메서드 실행
    // http://localhost:8080/api/settlements/monthly
    @GetMapping("/api/settlements/monthly")
    // URL에서 creatorId, month 값 받아오기
    public SettlementResponse getMonthlySettlement(@RequestParam String creatorId,
                                                   @RequestParam String month) {
        // 받은 값을 Service로 전달
        // Service에서:
        // - 강의 조회
        // - 판매 조회
        // - 환불 조회
        // - 정산 계산
        //
        // 수행 후 결과 반환
        return settlementService.getMonthlySettlement(creatorId, month);
    }

    // 운영자용 정산 내역 집계 API
    @GetMapping("/api/admin/settlements")
    public AdminSettlementSummaryResponse getAdminSettlementSummary(@RequestParam String startDate, String endDate) {
        return settlementService.getAdminSettlementSummary(startDate, endDate);
    }

    @PostMapping("/api/settlements/confirm")
    public String confirmSettlement(@RequestParam String creatorId,
                                    @RequestParam String month) {
        settlementService.confirmSettlement(creatorId, month);
        return "정산 확정 완료";
    }

    @PostMapping("/api/settlements/pay")
    public String paySettlement(@RequestParam String creatorId, @RequestParam String month) {
        settlementService.paySettlement(creatorId, month);
        return "지급 완료";
    }

    // 정산 내역 엑셀 다운로드 API
    @GetMapping("/api/admin/settlements/export")
    public ResponseEntity<byte[]> exportSettlement(@RequestParam String startDate, @RequestParam String endDate) {
        return settlementService.exportSettlement(startDate, endDate);
    }
}
