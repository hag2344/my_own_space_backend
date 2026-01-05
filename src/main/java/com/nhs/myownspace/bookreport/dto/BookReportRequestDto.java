package com.nhs.myownspace.bookreport.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookReportRequestDto {

    @NotBlank
    private String bookName;

    private String publisher;
    private String author;

    private String motive;
    private String plot;

    @NotBlank
    private String realization;

    @NotNull
    private String imagePath;


}
