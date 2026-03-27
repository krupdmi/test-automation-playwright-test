package org.testautomation.commons.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Type-conversion helpers used across step definitions and test-data builders.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ParserUtils {

    public static int toInt(String value) {
        return Integer.parseInt(value.trim());
    }

    public static long toLong(String value) {
        return Long.parseLong(value.trim());
    }

    public static double toDouble(String value) {
        return Double.parseDouble(value.trim());
    }

    public static BigDecimal toBigDecimal(String value) {
        return new BigDecimal(value.trim());
    }

    /**
     * Rounds a BigDecimal to {@code scale} decimal places using HALF_UP.
     */
    public static BigDecimal round(BigDecimal value, int scale) {
        return value.setScale(scale, RoundingMode.HALF_UP);
    }

    public static boolean toBoolean(String value) {
        return Boolean.parseBoolean(value.trim());
    }
}
