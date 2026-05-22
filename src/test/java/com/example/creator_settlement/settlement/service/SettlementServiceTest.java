package com.example.creator_settlement.settlement.service;

import com.example.creator_settlement.settlement.entity.Settlement;
import com.example.creator_settlement.settlement.entity.SettlementStatus;
import com.example.creator_settlement.settlement.repository.SettlementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.creator_settlement.settlement.dto.SettlementResponse;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SettlementServiceTest {
    @Autowired
    private SettlementService settlementService;

    @Autowired
    private SettlementRepository settlementRepository;

    @BeforeEach // 테스트 실행 전에 매번 실행
    void setUp() {
        settlementRepository.deleteAll(); // SETTLEMENT 테이블 초기화
    }

    @Test
    void creator1_3월_정산조회_테스트() {

        // 실행
        SettlementResponse result =
                settlementService.getMonthlySettlement(
                        "creator-1",
                        "2025-03"
                );

        // 검증
        assertEquals(260000, result.getTotalSalesAmount());
        assertEquals(110000, result.getTotalRefundAmount());
        assertEquals(120000, result.getPayoutAmount());
    }

    @Test
    void creator2_2월_월경계취소_반영() {
        SettlementResponse result =
                settlementService.getMonthlySettlement(
                        "creator-2",
                        "2025-02"
                );

        assertEquals(0, result.getTotalSalesAmount());
        assertEquals(60000, result.getTotalRefundAmount());
        assertEquals(-60000, result.getNetSalesAmount());
        assertEquals(-12000, result.getPlatformFeeAmount());
        assertEquals(-48000, result.getPayoutAmount());
    }

    @Test
    void creator3_빈월조회() {
        SettlementResponse result =
                settlementService.getMonthlySettlement(
                        "creator-3",
                        "2025-03"
                );

        assertEquals(0, result.getTotalSalesAmount());
        assertEquals(0, result.getPayoutAmount());
    }

    @Test
    void 정산_확정_후_지급_상태전이() {
        settlementService.getMonthlySettlement("creator-1", "2025-03");

        settlementService.confirmSettlement("creator-1", "2025-03");
        settlementService.paySettlement("creator-1", "2025-03");

        Settlement settlement = settlementRepository
                .findByCreatorIdAndMonth("creator-1", "2025-03")
                .get();

        assertEquals(SettlementStatus.PAID, settlement.getStatus());
        assertNotNull(settlement.getConfirmedAt());
        assertNotNull(settlement.getPaidAt());
    }

    @Test
    void PENDING_상태에서_지급_시도시_예외() {
        settlementService.getMonthlySettlement("creator-1", "2025-03");

        assertThrows(ResponseStatusException.class,
                () -> settlementService.paySettlement("creator-1", "2025-03"));
    }

    @Test
    void creator1_4월_동일월_다수취소() {
        SettlementResponse result =
                settlementService.getMonthlySettlement("creator-1", "2025-04");

        assertEquals(250000, result.getTotalSalesAmount());
        assertEquals(150000, result.getTotalRefundAmount());
        assertEquals(100000, result.getNetSalesAmount());
        assertEquals(15000,  result.getPlatformFeeAmount());
        assertEquals(85000,  result.getPayoutAmount());
        assertEquals(3, result.getSaleCount());
        assertEquals(2, result.getCancelCount());
    }

    @Test
    void 잘못된_연월_형식_예외() {
        assertThrows(ResponseStatusException.class,
                () -> settlementService.getMonthlySettlement("creator-1", "2025/03"));
    }
}
