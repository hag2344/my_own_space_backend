package com.nhs.myownspace.home.dto;

import lombok.*;

import java.time.OffsetDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class HomeBookReportItemDto {
    private Long id;
    private String bookName;
    private String thumbnailUrl;
    private OffsetDateTime createdAt;

}
