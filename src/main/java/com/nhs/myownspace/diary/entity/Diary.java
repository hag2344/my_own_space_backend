package com.nhs.myownspace.diary.entity;

import com.nhs.myownspace.user.Provider;
import com.nhs.myownspace.user.entity.User;
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
@Table(name = "diary",
        indexes = {
                @Index(name = "idx_diary_user_id", columnList = "user_id")
        })
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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
