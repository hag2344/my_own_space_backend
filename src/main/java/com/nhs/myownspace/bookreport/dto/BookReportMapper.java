package com.nhs.myownspace.bookreport.dto;

import com.nhs.myownspace.bookreport.entity.BookReport;
import com.nhs.myownspace.user.entity.User;

public class BookReportMapper {

    /**
     * Entity → Response DTO
     */
    public static BookReportResponseDto responseDto(BookReport bookReport,
                                                    String thumbnailUrl){
        return  BookReportResponseDto.builder()
                .id(bookReport.getId())
                .bookName(bookReport.getBookName())
                .publisher(bookReport.getPublisher())
                .author(bookReport.getAuthor())
                .motive(bookReport.getMotive())
                .plot(bookReport.getPlot())
                .realization(bookReport.getRealization())
                .imagePath(bookReport.getImagePath())
                .thumbnailUrl(thumbnailUrl)
                .createdAt(bookReport.getCreatedAt())
                .updatedAt(bookReport.getUpdatedAt())
                .build();
    }

    /**
     * Request DTO → Entity (생성 시)
     */
    public static BookReport createEntity(BookReportRequestDto req, Long userId){
        if(req == null) return  null;

        User userRef = User.builder()
                .id(userId)
                .build();

        return BookReport.builder()
                .user(userRef)
                .bookName(req.getBookName())
                .publisher(req.getPublisher())
                .author(req.getAuthor())
                .motive(req.getMotive())
                .plot(req.getPlot())
                .realization(req.getRealization())
                .imagePath(req.getImagePath())
                .build();
    }

    /**
     * Request DTO → Entity 적용 (수정 시)
     */
    public static void updateEntity(BookReport bookReport, BookReportRequestDto req) {
        if (bookReport == null || req == null) return;

        bookReport.setBookName(req.getBookName());
        bookReport.setPublisher(req.getPublisher());
        bookReport.setAuthor(req.getAuthor());
        bookReport.setMotive(req.getMotive());
        bookReport.setPlot(req.getPlot());
        bookReport.setRealization(req.getRealization());
        bookReport.setImagePath(req.getImagePath());
    }


}
