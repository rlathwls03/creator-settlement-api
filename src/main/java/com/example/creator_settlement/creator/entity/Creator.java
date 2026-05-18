package com.example.creator_settlement.creator.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED) // JPA는 기본 생성자가 필요하지만, 객체의 무분별한 생성 막기 위해 protected
@Table(name = "creators")
public class Creator { // 크리에이터
    @Id
    private String id; // 크리에이터 ID

    @Column(nullable = false)
    private String name; // 크리에이터 이름

    public Creator(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
