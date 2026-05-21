package com.example.creator_settlement.settlement.service;

import com.example.creator_settlement.creator.entity.Creator;
import com.example.creator_settlement.creator.repository.CreatorRepository;
import com.example.creator_settlement.settlement.dto.AdminSettlementResponse;
import com.example.creator_settlement.settlement.dto.AdminSettlementSummaryResponse;
import com.example.creator_settlement.cancellation.entity.CancelRecord;
import com.example.creator_settlement.cancellation.repository.CancelRecordRepository;
import com.example.creator_settlement.course.entity.Course;
import com.example.creator_settlement.course.repository.CourseRepository;
import com.example.creator_settlement.sale.entity.SaleRecord;
import com.example.creator_settlement.sale.repository.SaleRecordRepository;
import com.example.creator_settlement.settlement.dto.SettlementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor // lombok이 생성자 주입 자동화
public class SettlementService {
    // [임시]수수료율 20% 고정
    private static final int PLATFORM_FEE_RATE = 20;

    // creatorId와 month를 받음
    // 해당 creator의 강의 목록 조회
    private final CourseRepository courseRepository; // final -> 한 번 주입되면 변경 불가

    // 해당 강의들의 판매 내역 조회
    private final SaleRecordRepository saleRecordRepository;

    // 해당 강의들의 취소 내역 조회
    private final CancelRecordRepository cancelRecordRepository;

    private final CreatorRepository creatorRepository;

    public SettlementResponse getMonthlySettlement(String creatorId, String month) {
        // "2025-03" 같은 문자열을 YearMonth 객체로 변환
        // -> 연도 + 월만 관리하는 Java 클래스
        YearMonth yearMonth = YearMonth.parse(month);

        // 조회 시작일 계산
        // 예: 2025-03-01 00:00:00 +09:00
        // 즉, 해당 월의 첫날 시작 시간
        OffsetDateTime startDate = yearMonth
                .atDay(1) // 1일로 설정
                .atStartOfDay() // 00:00:00으로 설정
                .atOffset(ZoneOffset.ofHours(9)); // 한국 시간(KST)

        // 조회 종료일 계산
        // 예: 2025-03-31 23:59:59 +09:00
        // 즉, 해당 월의 마지막 날 끝 시간
        OffsetDateTime endDate = yearMonth
                .atEndOfMonth() // 해당 월의 마지막 날짜
                .atTime(23, 59, 59) // 하루의 마지막 시간
                .atOffset(ZoneOffset.ofHours(9)); // 한국 시간(KST)

        // 1. creatorId로 해당 크리에이터의 강의 목록 조회
        List<Course> courses = courseRepository.findByCreatorId(creatorId);

        // 2. 조회한 강의들에서 courseId만 추출
        // 이유: SaleRecord는 creatorId가 없고 courseId만 있기 때문
        List<String> courseIds = courses.stream()
                .map(Course::getId)
                .toList(); // Course 객체 -> id만 꺼냄

        // 3. 해당 강의들의 판매 내역 조회
        List<SaleRecord> sales = saleRecordRepository.findByCourseIdInAndPaidAtBetween(courseIds, startDate, endDate);

        // 4. 판매 내역에서 saleRecordId만 추출
        // CancelRecord는 saleRecordId를 기준으로 연결
        List<String> saleRecordIds = sales.stream()
                .map(SaleRecord::getId)
                .toList();

        // 5. 해당 판매들의 취소 내역 조회
        List<CancelRecord> cancels = cancelRecordRepository.findBySaleRecordIdInAndCanceledAtBetween(saleRecordIds, startDate, endDate);

        // 6. 총 판매 금액 계산
        long totalSalesAmount = sales.stream().mapToLong(SaleRecord::getAmount).sum();

        // 7. 총 환불 금액 계산
        long totalRefundAmount = cancels.stream().mapToLong(CancelRecord::getRefundAmount).sum();

        // 8. 순 판매 금액 계산
        long netSalesAmount = totalSalesAmount - totalRefundAmount;

        // 9. 플랫폼 수수료 계산
        long platformFeeAmount = netSalesAmount * PLATFORM_FEE_RATE / 100;

        // 10. 실제 정산 예정 금액 계산
        long payoutAmount = netSalesAmount - platformFeeAmount;

        // 11. 최종 결과를 DTO에 담아서 반환
        // 이 객체가 JSON 응답으로 변환됨
        return new SettlementResponse(
            creatorId, // 크리에이터 ID
            month, // 조회 월

            totalSalesAmount, // 총 판매 금액
            totalRefundAmount, // 총 환불 금액
            netSalesAmount, // 순 판매 금액

            platformFeeAmount, // 플랫폼 수수료
            payoutAmount, // 실제 정산 금액

            sales.size(), // 판매 건수
            cancels.size() // 취소 건수
        );
    }

    public AdminSettlementSummaryResponse getAdminSettlementSummary(String startDate, String endDate) {
        OffsetDateTime start = OffsetDateTime.parse(startDate);
        OffsetDateTime end = OffsetDateTime.parse(endDate);

        List<Creator> creators = creatorRepository.findAll();

        List<AdminSettlementResponse> settlements = creators.stream()
                .map(creator -> {
                    List<Course> courses = courseRepository.findByCreatorId(creator.getId());

                    List<String> courseIds = courses.stream()
                            .map(Course::getId)
                            .toList();

                    List<SaleRecord> sales = saleRecordRepository
                            .findByCourseIdInAndPaidAtBetween(courseIds, start, end);

                    List<String> saleRecordIds = sales.stream()
                            .map(SaleRecord::getId)
                            .toList();

                    List<CancelRecord> cancels = cancelRecordRepository
                            .findBySaleRecordIdInAndCanceledAtBetween(saleRecordIds, start, end);

                    long totalSalesAmount = sales.stream()
                            .mapToLong(SaleRecord::getAmount)
                            .sum();

                    long totalRefundAmount = cancels.stream()
                            .mapToLong(CancelRecord::getRefundAmount)
                            .sum();

                    long netSalesAmount = totalSalesAmount - totalRefundAmount;
                    long platformFeeAmount = netSalesAmount * PLATFORM_FEE_RATE / 100;
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
}
