package com.mall.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String ORDER_NO_PATTERN = "yyyyMMddHHmmss";

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)) : null;
    }

    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DateTimeFormatter.ofPattern(DATE_PATTERN)) : null;
    }

    public static String nowDateTimeStr() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
    }

    public static String nowDateStr() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_PATTERN));
    }
}
