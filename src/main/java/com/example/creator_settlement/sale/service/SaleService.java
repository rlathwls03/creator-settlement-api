package com.example.creator_settlement.sale.service;

import com.example.creator_settlement.course.entity.Course;
import com.example.creator_settlement.course.repository.CourseRepository;
import com.example.creator_settlement.sale.dto.SaleResponse;
import com.example.creator_settlement.sale.entity.SaleRecord;
import com.example.creator_settlement.sale.repository.SaleRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleService {
    private final CourseRepository courseRepository;
    private final SaleRecordRepository saleRecordRepository;

    // 크리에이터별 기간별 판매내역 조회
    public List<SaleResponse> getSales(
            String creatorId,
            OffsetDateTime startDate,
            OffsetDateTime endDate
    ) {
        // 크리에이터가 가진 강의 찾기
        List<Course> courses = courseRepository.findByCreatorId(creatorId);
        // 강의 객체 전체가 아닌 판매 조회에 필요한 courseId만 추출
        List<String> courseIds = courses.stream()
                .map(Course::getId)
                .toList();
        // 강의들의 판매 내역 중, 결제일이 조회 기간 안에 있는 것만 가져옴
        List<SaleRecord> sales = saleRecordRepository.findByCourseIdInAndPaidAtBetween(
                courseIds,
                startDate,
                endDate
        );
        // Entity를 DTO로 변환하여 반환
        return sales.stream()
                .map(s ->
                        new SaleResponse(
                                s.getId(),
                                s.getCourseId(),
                                s.getStudentId(),
                                s.getAmount(),
                                s.getPaidAt()
                        )
                )
                .toList();
    }
}
