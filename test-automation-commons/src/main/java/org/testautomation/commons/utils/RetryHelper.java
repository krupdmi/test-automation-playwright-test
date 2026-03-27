package org.testautomation.commons.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * Simple retry utility for flaky operations (e.g. polling DB state, waiting for async events).
 * Prefer {@link org.awaitility.Awaitility} for time-based polling; use this for counted retries.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RetryHelper {

    /**
     * Executes {@code action} up to {@code maxAttempts} times, sleeping {@code delayMs} between each.
     * Returns the first non-null result, or throws the last exception if all attempts fail.
     */
    public static <T> T retry(int maxAttempts, long delayMs, Supplier<T> action) {
        Exception last = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                T result = action.get();
                if (result != null) return result;
                log.debug("Retry attempt {}/{} — null result, retrying", attempt, maxAttempts);
            } catch (Exception e) {
                last = e;
                log.warn("Retry attempt {}/{} failed: {}", attempt, maxAttempts, e.getMessage());
            }
            if (attempt < maxAttempts) sleep(delayMs);
        }
        throw new IllegalStateException("All " + maxAttempts + " retry attempts exhausted", last);
    }

    /**
     * Executes {@code action} up to {@code maxAttempts} times, ignoring exceptions until the last.
     */
    public static void retryVoid(int maxAttempts, long delayMs, Runnable action) {
        retry(maxAttempts, delayMs, () -> { action.run(); return Boolean.TRUE; });
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
