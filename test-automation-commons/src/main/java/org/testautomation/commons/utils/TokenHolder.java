package org.testautomation.commons.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Thread-local storage for the authenticated user token.
 * Guarantees each parallel scenario thread has its own isolated token —
 * no race conditions from shared {@code System.setProperty} usage.
 *
 * <p>Set in a {@code @Before} hook tagged {@code @userToken};
 * clear in the corresponding {@code @After} hook.</p>
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TokenHolder {

    private static final ThreadLocal<String> USER_TOKEN = new ThreadLocal<>();

    public static String getUserToken() {
        String token = USER_TOKEN.get();
        if (token == null || token.isBlank()) {
            throw new IllegalStateException(
                    "User token not set for thread '" + Thread.currentThread().getName() +
                    "'. Ensure the scenario has the @userToken tag and the Before hook ran.");
        }
        return token;
    }

    public static void setUserToken(String token) {
        USER_TOKEN.set(token);
        log.debug("User token set for thread '{}'", Thread.currentThread().getName());
    }

    public static void clearUserToken() {
        USER_TOKEN.remove();
        log.debug("User token cleared for thread '{}'", Thread.currentThread().getName());
    }
}
