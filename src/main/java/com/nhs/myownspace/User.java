package com.nhs.myownspace;
// jakarta.persistence.* JPA 애노테이션들(@Entity, @Id 등)

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity // 이 클래스를 DB 테이블로 취급
@Table(name = "users")
@Getter // Lombok : getter/setter 자동 생성 (코드 줄임)
@Setter
public class User {
    @Id // 기본 키(Primary Key) 필드
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PK를 DB가 자동 증가(auto-increment)로 생성 (예: MySQL AUTO_INCREMENT)
    private Long id; // PK 컬럼 (BIGINT)

    private String name; // 일반 컬럼 (VARCHAR(255) 기본)
}
