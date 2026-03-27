package org.testautomation.commons.utils.dateandtime;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.testautomation.core.constants.DateTimeFormatConstants.*;

/**
 * Helpers for formatting and parsing date/time values used in test assertions.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTimeFormatterHelper {

    public static String nowFormatted() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(DD_MM_YYYY_HH_MM_SS));
    }

    public static String todayFormatted() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(DD_MM_YYYY));
    }

    public static String format(LocalDateTime dateTime, String pattern) {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalDateTime parseIso(String value) {
        return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(ISO_DATE_TIME));
    }

    public static long toEpochMillis(LocalDateTime dateTime, ZoneId zone) {
        return ZonedDateTime.of(dateTime, zone).toInstant().toEpochMilli();
    }

    public static String timestampSuffix() {
        return LocalDateTime.now().format(TIMESTAMP);
    }
}
