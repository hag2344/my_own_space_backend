package com.nhs.myownspace.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
public class PagingResponse<T> {
    private List<T> items;
    private boolean hasNext;
    private int totalPages;
    private long totalElements;

    public static <T> PagingResponse<T> of(Page<T> page) {
        return new PagingResponse<>(
                page.getContent(),
                page.hasNext(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}
