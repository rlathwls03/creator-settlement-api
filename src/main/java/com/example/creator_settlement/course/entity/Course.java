package com.example.creator_settlement.course.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "courses")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Course { // 강의
    @Id
    private String id; // 강의 ID

    @Column(nullable = false)
    private String creatorId; // 강의를 맡은 크리에이터 ID

    @Column(nullable = false)
    private String title; // 강의 제목

    public Course(String id, String creatorId, String title) {
        this.id = id;
        this.creatorId = creatorId;
        this.title = title;
    }
}
