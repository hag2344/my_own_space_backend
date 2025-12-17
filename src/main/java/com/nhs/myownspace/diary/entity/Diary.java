package com.nhs.myownspace.diary.entity;

import com.nhs.myownspace.user.Provider;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "diary")
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 소셜 로그인 제공자
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Provider provider;

    // 제공자별 유저 식별자 (카카오 id 등)
    @Column(name = "provider_id", nullable = false, length = 100)
    private String providerId;

    // 일기 제목
    @Column(nullable = false, length = 100)
    private String title;

    // yyyy-MM-dd
    @Column(name = "today_date", nullable = false)
    private LocalDate todayDate;

    @Column(name = "weather_id")
    private int weatherId;

    // HH:mm
    @Column(name = "wake_up_time")
    private LocalTime wakeUpTime;

    // HH:mm
    @Column(name = "sleep_time")
    private LocalTime sleepTime;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "today_important_work" ,columnDefinition = "TEXT")
    private String todayImportantWork;

    @Column(name = "today_kind_work" ,columnDefinition = "TEXT")
    private String todayKindWork;

    @Column(name = "today_self_reflection" ,columnDefinition = "TEXT")
    private String todaySelfReflection;

    @Column(name = "tomorrow_work" ,columnDefinition = "TEXT")
    private String tomorrowWork;

    // 생성일시
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 수정일시
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
