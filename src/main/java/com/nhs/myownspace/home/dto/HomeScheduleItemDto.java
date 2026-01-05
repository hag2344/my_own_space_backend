package com.nhs.myownspace.home.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class HomeScheduleItemDto {
    private Long id;
    private String title;
    private LocalDateTime startDate;
}
