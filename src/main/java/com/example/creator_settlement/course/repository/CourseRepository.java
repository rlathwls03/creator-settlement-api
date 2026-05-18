package com.example.creator_settlement.course.repository;

import com.example.creator_settlement.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// 특정 크리에이터가 만든 강의 목록을 조회하는 Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    List<Course> findByCreatorId(String creatorId);
}
