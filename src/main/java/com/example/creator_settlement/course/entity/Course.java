package com.example.creator_settlement.course.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "course")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Course { // 강의
    @Id
    private String courseId; // 강의 ID

    @Column(nullable = false)
    private String creatorId; // 강의를 맡은 크리에이터 ID

    @Column(nullable = false)
    private String courseTitle; // 강의 제목

    public Course(String courseId, String creatorId, String courseTitle) {
        this.courseId = courseId;
        this.creatorId = creatorId;
        this.courseTitle = courseTitle;
    }
}
