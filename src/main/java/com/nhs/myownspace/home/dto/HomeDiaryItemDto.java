package com.nhs.myownspace.home.dto;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class HomeDiaryItemDto {
    private Long id;
    private String title;
    private LocalDate todayDate;
}
