package org.testautomation.core.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTimeFormatConstants {
    public static final String DD_MM_YYYY_HH_MM_SS = "dd/MM/yyyy HH:mm:ss";
    public static final String DD_MM_YYYY          = "dd/MM/yyyy";
    public static final String YYYY_MM_DD          = "yyyy-MM-dd";
    public static final String ISO_DATE_TIME       = "yyyy-MM-dd'T'HH:mm:ss";
    public static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
}
