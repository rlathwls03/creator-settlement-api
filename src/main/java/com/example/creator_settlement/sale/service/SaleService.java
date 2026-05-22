package com.example.creator_settlement.sale.service;

import com.example.creator_settlement.course.entity.Course;
import com.example.creator_settlement.course.repository.CourseRepository;
import com.example.creator_settlement.sale.dto.SaleResponse;
import com.example.creator_settlement.sale.entity.SaleRecord;
import com.example.creator_settlement.sale.repository.SaleRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        LocalDateTime start = startDate.toLocalDateTime();
        LocalDateTime end = endDate.toLocalDateTime();

        List<Course> courses = courseRepository.findByCreatorId(creatorId);
        List<String> courseIds = courses.stream()
                .map(Course::getId)
                .toList();
        List<SaleRecord> sales = saleRecordRepository.findByCourseIdInAndPaidAtGreaterThanEqualAndPaidAtLessThan(
                courseIds,
                start,
                end
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
