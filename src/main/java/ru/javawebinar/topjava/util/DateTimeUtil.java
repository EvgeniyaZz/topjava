package ru.javawebinar.topjava.util;

import org.springframework.format.Formatter;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateTimeUtil {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // DB doesn't support LocalDate.MIN/MAX
    private static final LocalDateTime MIN_DATE = LocalDateTime.of(1, 1, 1, 0, 0);
    private static final LocalDateTime MAX_DATE = LocalDateTime.of(3000, 1, 1, 0, 0);

    private DateTimeUtil() {
    }

    public static LocalDateTime atStartOfDayOrMin(LocalDate localDate) {
        return localDate != null ? localDate.atStartOfDay() : MIN_DATE;
    }

    public static LocalDateTime atStartOfNextDayOrMax(LocalDate localDate) {
        return localDate != null ? localDate.plusDays(1).atStartOfDay() : MAX_DATE;
    }

    public static String toString(LocalDateTime ldt) {
        return ldt == null ? "" : ldt.format(DATE_TIME_FORMATTER);
    }

    public static @Nullable
    LocalDate parseLocalDate(@Nullable String str) {
        return StringUtils.hasLength(str) ? LocalDate.parse(str) : null;
    }

    public static @Nullable
    LocalTime parseLocalTime(@Nullable String str) {
        return StringUtils.hasLength(str) ? LocalTime.parse(str) : null;
    }

    public static class CustomDateFormatter implements Formatter<LocalDate> {
        @Override
        public LocalDate parse(String text, Locale locale) {
            return text.length() != 0 ? LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;
        }

        @Override
        public String print(LocalDate localDate, Locale locale) {
            return localDate.toString();
        }
    }

    public static class CustomTimeFormatter implements Formatter<LocalTime> {
        @Override
        public LocalTime parse(String text, Locale locale) {
            return text.length() != 0 ? LocalTime.parse(text, DateTimeFormatter.ofPattern("HH:mm")) : null;
        }

        @Override
        public String print(LocalTime localTime, Locale locale) {
            return localTime.toString();
        }
    }
}
