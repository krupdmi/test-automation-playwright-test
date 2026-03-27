package org.testautomation.commons.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * General-purpose utility methods that don't belong to a more specific utility class.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonUtils {

    /** Generates a random UUID string (no dashes). */
    public static String randomId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /** Returns {@code value} if non-null and non-blank, otherwise {@code fallback}. */
    public static String orDefault(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }

    /** Masks all but the last {@code visibleChars} characters with {@code '*'}. */
    public static String mask(String value, int visibleChars) {
        if (value == null || value.length() <= visibleChars) return value;
        return "*".repeat(value.length() - visibleChars) + value.substring(value.length() - visibleChars);
    }
}
