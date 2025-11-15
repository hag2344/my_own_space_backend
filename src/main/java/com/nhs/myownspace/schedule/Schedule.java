package com.nhs.myownspace.schedule;

import com.nhs.myownspace.user.Provider;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "schedule")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자 식별
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Provider provider;

    @Column(name = "provider_id", nullable = false, length = 100)
    private String providerId;

    // 일정 제목
    @Column(nullable = false, length = 100)
    private String title;

    // yyyy-MM-dd 또는 yyyy-MM-ddTHH:mm:ss
    @Column(name = "start_date", nullable = false)
    private String startDate;

    @Column(name = "end_date")
    private String  endDate;

    @Column(name = "all_day")
    private boolean allDay;

    @Column(length = 100)
    private String location;

    @Column(length = 20)
    private String color;    // ex. "#FF5733", "red"

    @Column(columnDefinition = "TEXT")
    private String description;
}