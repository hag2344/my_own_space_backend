package com.nhs.myownspace.schedule.entity;

import com.nhs.myownspace.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "schedule",
        indexes = {
                @Index(name = "idx_schedule_user_id", columnList = "user_id")
        })
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 일정 제목
    @Column(nullable = false, length = 100)
    private String title;

    // yyyy-MM-dd 또는 yyyy-MM-ddTHH:mm:ss
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "all_day")
    private boolean allDay;

    @Column(length = 100)
    private String location;

    // ex. "#FF5733", "red"
    @Column(length = 20)
    private String color;

    @Column(columnDefinition = "TEXT")
    private String description;

    // 생성일시
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 수정일시
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}