package org.testautomation.commons.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.assertj.core.api.SoftAssertions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * Assertion helpers that collect failures via AssertJ {@link SoftAssertions}
 * so all field mismatches are reported in a single test failure.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ComparatorUtils {

    /**
     * Asserts that {@code actual} equals {@code expected}, labelling the failure with {@code field}.
     * All assertions are collected — call {@code softly.assertAll()} after your last check.
     */
    public static void assertEquals(SoftAssertions softly, String field, Object expected, Object actual) {
        softly.assertThat(actual)
                .as("Field '%s': expected <%s> but was <%s>", field, expected, actual)
                .isEqualTo(expected);
    }

    /**
     * Asserts that {@code actual} is within {@code delta} of {@code expected}.
     */
    public static void assertBigDecimalClose(SoftAssertions softly, String field,
                                             BigDecimal expected, BigDecimal actual, BigDecimal delta) {
        if (expected == null && actual == null) return;
        softly.assertThat(actual)
                .as("Field '%s': expected ~<%s> ± %s but was <%s>", field, expected, delta, actual)
                .isNotNull()
                .satisfies(a -> softly.assertThat(a.subtract(expected).abs())
                        .isLessThanOrEqualTo(delta));
    }

    /**
     * Asserts that {@code actual} contains all elements of {@code expectedSubset}.
     */
    public static <T> void assertContainsAll(SoftAssertions softly, String field,
                                              List<T> expectedSubset, List<T> actual) {
        softly.assertThat(actual)
                .as("Field '%s'", field)
                .containsAll(expectedSubset);
    }

    /**
     * Asserts the actual string value is in the allowed set.
     */
    public static void assertInAllowedValues(SoftAssertions softly, String field,
                                              String actual, Set<String> allowed) {
        softly.assertThat(allowed)
                .as("Field '%s': <%s> not in allowed values %s", field, actual, allowed)
                .contains(actual);
    }
}
