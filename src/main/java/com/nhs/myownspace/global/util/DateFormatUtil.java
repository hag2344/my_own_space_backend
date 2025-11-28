package com.nhs.myownspace.global.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DateFormatUtil {

    public static LocalDateTime toDateTime(String value, boolean isAllDay, boolean isEnd) {
        if (value == null || value.isEmpty())
            return null;

        if (isAllDay) {
            // 하루종일일 때는 시간 고정
            LocalDate date = LocalDate.parse(value);

            if (isEnd) {
                return date.atTime(23, 59);
            }
            return date.atStartOfDay();
        }

        // 날짜+시간
        return LocalDateTime.parse(value);
    }

    public static String toStr(LocalDateTime dt, boolean allDay) {
        if (dt == null) return null;

        if (allDay) {
            return dt.toLocalDate().toString();
        }

        return dt.toString(); // "2025-02-12T14:30"
    }
}
