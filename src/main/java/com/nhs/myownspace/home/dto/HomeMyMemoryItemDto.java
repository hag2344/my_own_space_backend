package com.nhs.myownspace.home.dto;

import lombok.*;
import java.time.OffsetDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class HomeMyMemoryItemDto {
    private Long id;
    private String title;
    private String thumbnailUrl;
    private OffsetDateTime createdAt;
}
