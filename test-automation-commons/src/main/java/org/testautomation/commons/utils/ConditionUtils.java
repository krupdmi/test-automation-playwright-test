package org.testautomation.commons.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Null/blank checks and common boolean guards.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConditionUtils {

    public static boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

    public static boolean isNotNullOrBlank(String value) {
        return !isNullOrBlank(value);
    }

    public static boolean isNull(Object value) {
        return value == null;
    }

    public static boolean isNotNull(Object value) {
        return value != null;
    }
}
