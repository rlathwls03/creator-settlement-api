package com.example.creator_settlement.settlement.service;

import com.example.creator_settlement.creator.entity.Creator;
import com.example.creator_settlement.creator.repository.CreatorRepository;
import com.example.creator_settlement.fee.entity.FeePolicy;
import com.example.creator_settlement.fee.repository.FeePolicyRepository;
import com.example.creator_settlement.settlement.dto.AdminSettlementResponse;
import com.example.creator_settlement.settlement.dto.AdminSettlementSummaryResponse;
import com.example.creator_settlement.cancellation.entity.CancelRecord;
import com.example.creator_settlement.cancellation.repository.CancelRecordRepository;
import com.example.creator_settlement.course.entity.Course;
import com.example.creator_settlement.course.repository.CourseRepository;
import com.example.creator_settlement.sale.entity.SaleRecord;
import com.example.creator_settlement.sale.repository.SaleRecordRepository;
import com.example.creator_settlement.settlement.dto.SettlementResponse;
import com.example.creator_settlement.settlement.entity.Settlement;
import com.example.creator_settlement.settlement.entity.SettlementStatus;
import com.example.creator_settlement.settlement.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor // lombok이 생성자 주입 자동화
public class SettlementService {
    // [임시]수수료율 20% 고정
//    private static final int PLATFORM_FEE_RATE = 20;

    // creatorId와 month를 받음
    // 해당 creator의 강의 목록 조회
    private final CourseRepository courseRepository; // final -> 한 번 주입되면 변경 불가

    // 해당 강의들의 판매 내역 조회
    private final SaleRecordRepository saleRecordRepository;

    // 해당 강의들의 취소 내역 조회
    private final CancelRecordRepository cancelRecordRepository;

    private final CreatorRepository creatorRepository;

    private final SettlementRepository settlementRepository;

    // 수수료 정책
    private final FeePolicyRepository feePolicyRepository;

    public SettlementResponse getMonthlySettlement(String creatorId, String month) {
        // "2025-03" 같은 문자열을 YearMonth 객체로 변환
        // -> 연도 + 월만 관리하는 Java 클래스
        YearMonth yearMonth;

        try {
            yearMonth = YearMonth.parse(month);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "month 형식은 yyyy-MM 입니다."
            );
        }

        // 동일 기간 중복 정산 방지
        var existingSettlement = settlementRepository.findByCreatorIdAndMonth(creatorId, month);

        if (existingSettlement.isPresent()) {
            Settlement s = existingSettlement.get();

            return new SettlementResponse(
                    s.getCreatorId(),
                    s.getMonth(),
                    s.getTotalSalesAmount(),
                    s.getTotalRefundAmount(),
                    s.getNetSalesAmount(),
                    s.getPlatformFeeAmount(),
                    s.getPayoutAmount(),
                    s.getSaleCount(),
                    s.getCancelCount()
            );
        }

        // 해당 월 1일 00:00:00 이상, 다음 월 1일 00:00:00 미만으로 조회
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

        // 1. creatorId로 해당 크리에이터의 강의 목록 조회
        List<Course> courses = courseRepository.findByCreatorId(creatorId);

        // 2. 조회한 강의들에서 courseId만 추출
        // 이유: SaleRecord는 creatorId가 없고 courseId만 있기 때문
        List<String> courseIds = courses.stream()
                .map(Course::getId)
                .toList(); // Course 객체 -> id만 꺼냄

        // 3. 해당 강의들의 판매 내역 조회
        List<SaleRecord> sales = saleRecordRepository.findByCourseIdInAndPaidAtGreaterThanEqualAndPaidAtLessThan(courseIds, startDate, endDate);

        // 4. 판매 내역에서 saleRecordId만 추출
        // CancelRecord는 saleRecordId를 기준으로 연결
        List<SaleRecord> allCreatorSales =
                saleRecordRepository.findByCourseIdIn(courseIds);

        List<String> allCreatorSaleIds = allCreatorSales.stream()
                .map(SaleRecord::getId)
                .toList();

        List<CancelRecord> cancels = allCreatorSaleIds.isEmpty()
                ? List.of()
                : cancelRecordRepository.findBySaleRecordIdInAndCanceledAtGreaterThanEqualAndCanceledAtLessThan(
                allCreatorSaleIds,
                startDate,
                endDate
        );

        // 6. 총 판매 금액 계산
        long totalSalesAmount = sales.stream().mapToLong(SaleRecord::getAmount).sum();

        // 7. 총 환불 금액 계산
        long totalRefundAmount = cancels.stream().mapToLong(CancelRecord::getRefundAmount).sum();

        // 8. 순 판매 금액 계산
        long netSalesAmount = totalSalesAmount - totalRefundAmount;

        // 9. 플랫폼 수수료 계산
        LocalDate feeTargetDate = startDate.toLocalDate();

        FeePolicy feePolicy = feePolicyRepository
                .findFirstByEffectiveFromLessThanEqualAndEffectiveToGreaterThanEqual(
                        feeTargetDate,
                        feeTargetDate
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        "적용 가능한 수수료 정책이 없습니다."
                ));

        int feeRate = feePolicy.getFeeRate();

        long platformFeeAmount = netSalesAmount * feeRate / 100;

        // 10. 실제 정산 예정 금액 계산
        long payoutAmount = netSalesAmount - platformFeeAmount;

        // 11. 최종 결과를 DTO에 담아서 반환
        // 이 객체가 JSON 응답으로 변환됨
        Settlement settlement = new Settlement(
                creatorId + "-" + month,
                creatorId,
                month,

                totalSalesAmount,
                totalRefundAmount,
                netSalesAmount,

                platformFeeAmount,
                payoutAmount,

                sales.size(),
                cancels.size(),

                SettlementStatus.PENDING,

                OffsetDateTime.now(),

                null,
                null
        );

        settlementRepository.save(settlement);

        return new SettlementResponse(
                creatorId,
                month,

                totalSalesAmount,
                totalRefundAmount,
                netSalesAmount,

                platformFeeAmount,
                payoutAmount,

                sales.size(),
                cancels.size()
        );
    }

    public AdminSettlementSummaryResponse getAdminSettlementSummary(String startDate, String endDate) {
        LocalDateTime start = OffsetDateTime.parse(startDate).toLocalDateTime();
        LocalDateTime end = OffsetDateTime.parse(endDate).toLocalDateTime();

        List<Creator> creators = creatorRepository.findAll();

        List<AdminSettlementResponse> settlements = creators.stream()
                .map(creator -> {
                    List<Course> courses = courseRepository.findByCreatorId(creator.getId());

                    List<String> courseIds = courses.stream()
                            .map(Course::getId)
                            .toList();

                    List<SaleRecord> sales = saleRecordRepository
                            .findByCourseIdInAndPaidAtGreaterThanEqualAndPaidAtLessThan(courseIds, start, end);

                    List<SaleRecord> allCreatorSales =
                            saleRecordRepository.findByCourseIdIn(courseIds);

                    List<String> allCreatorSaleIds = allCreatorSales.stream()
                            .map(SaleRecord::getId)
                            .toList();

                    List<CancelRecord> cancels =
                            allCreatorSaleIds.isEmpty()
                                    ? List.of()
                                    : cancelRecordRepository.findBySaleRecordIdInAndCanceledAtGreaterThanEqualAndCanceledAtLessThan(
                                    allCreatorSaleIds,
                                    start,
                                    end
                            );

                    long totalSalesAmount = sales.stream()
                            .mapToLong(SaleRecord::getAmount)
                            .sum();

                    long totalRefundAmount = cancels.stream()
                            .mapToLong(CancelRecord::getRefundAmount)
                            .sum();

                    long netSalesAmount = totalSalesAmount - totalRefundAmount;

                    LocalDate feeTargetDate = start.toLocalDate();

                    FeePolicy feePolicy =
                            feePolicyRepository
                                    .findFirstByEffectiveFromLessThanEqualAndEffectiveToGreaterThanEqual(
                                            feeTargetDate,
                                            feeTargetDate
                                    )
                                    .orElseThrow(() -> new ResponseStatusException(
                                            HttpStatus.UNPROCESSABLE_ENTITY,
                                            "수수료 정책 없음"
                                    ));

                    int feeRate = feePolicy.getFeeRate();

                    long platformFeeAmount =
                            netSalesAmount * feeRate / 100;
                    long payoutAmount = netSalesAmount - platformFeeAmount;

                    return new AdminSettlementResponse(
                            creator.getId(),
                            payoutAmount
                    );
                })
                .toList();

        long totalPayout = settlements.stream().mapToLong(AdminSettlementResponse::getPayoutAmount).sum();

        return new AdminSettlementSummaryResponse(
                totalPayout,
                settlements
        );
    }

    public void confirmSettlement(String creatorId, String month) {
        Settlement settlement = settlementRepository
                .findByCreatorIdAndMonth(creatorId, month)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "정산을 찾을 수 없습니다."
                ));

        try {
            settlement.confirm();
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    e.getMessage()
            );
        }

        settlementRepository.save(settlement);
    }

    public void paySettlement(String creatorId, String month) {
        Settlement settlement = settlementRepository
                .findByCreatorIdAndMonth(creatorId, month)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "정산을 찾을 수 없습니다."
                ));

        try {
            settlement.pay();
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    e.getMessage()
            );
        }

        settlementRepository.save(settlement);
    }

    // 정산 내역 엑셀 다운로드
    public ResponseEntity<byte[]> exportSettlement(String startDate, String endDate) {
        AdminSettlementSummaryResponse result = getAdminSettlementSummary(startDate, endDate);

        StringBuilder csv = new StringBuilder();

        csv.append("creatorId, payoutAmount\n");

        for (AdminSettlementResponse settlement : result.getSettlements()) {
            csv.append(settlement.getCreatorId()).append(",").append(settlement.getPayoutAmount()).append("\n");
        }

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=settlements.csv")
                .body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }
}
