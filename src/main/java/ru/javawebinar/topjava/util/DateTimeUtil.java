package ru.javawebinar.topjava.util;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class DateTimeUtil {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static boolean isBetweenHalfOpen(TemporalAccessor t, TemporalAccessor start, TemporalAccessor end) {
        try {
            LocalDateTime ltd = LocalDateTime.from(t);

            return ltd.compareTo(start == null ? LocalDateTime.MIN : LocalDateTime.from(start)) >= 0 &&
                    ltd.compareTo(end == null ? LocalDateTime.MAX : LocalDateTime.from(end)) < 0;
        } catch (DateTimeException e) {
            LocalTime lt = LocalTime.from(t);

            return lt.compareTo(start == null ? LocalTime.MIN : LocalTime.from(start)) >= 0 &&
                    lt.compareTo(end == null ? LocalTime.MAX : LocalTime.from(end)) < 0;
        }
    }

    public static String toString(LocalDateTime ldt) {
        return ldt == null ? "" : ldt.format(DATE_TIME_FORMATTER);
    }
}

