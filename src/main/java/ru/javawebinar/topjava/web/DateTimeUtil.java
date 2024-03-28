package ru.javawebinar.topjava.web;

import org.springframework.format.Formatter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateTimeUtil {

    public static class CustomDateFormatter implements Formatter<LocalDate> {
        @Override
        public LocalDate parse(String text, Locale locale) {
            return LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        @Override
        public String print(LocalDate localDate, Locale locale) {
            return localDate.toString();
        }
    }

    public static class CustomTimeFormatter implements Formatter<LocalTime> {
        @Override
        public LocalTime parse(String text, Locale locale) {
            return LocalTime.parse(text, DateTimeFormatter.ofPattern("HH:mm"));
        }

        @Override
        public String print(LocalTime localTime, Locale locale) {
            return localTime.toString();
        }
    }
}