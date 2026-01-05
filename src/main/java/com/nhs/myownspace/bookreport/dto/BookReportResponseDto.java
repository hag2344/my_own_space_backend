package com.nhs.myownspace.bookreport.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
public class BookReportResponseDto {

    private Long id;

    private String bookName;
    private String publisher;
    private String author;

    private String motive;
    private String plot;
    private String realization;

    private String imagePath;
    private String thumbnailUrl;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
