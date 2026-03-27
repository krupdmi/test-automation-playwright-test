package org.testautomation.commons.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Named time constants in both seconds and milliseconds for use in
 * waits, retries, and Awaitility polls.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GenericTimeConstants {

    // Seconds
    public static final int ONE_SECOND     = 1;
    public static final int TWO_SECONDS    = 2;
    public static final int THREE_SECONDS  = 3;
    public static final int FIVE_SECONDS   = 5;
    public static final int TEN_SECONDS    = 10;
    public static final int FIFTEEN_SECONDS = 15;
    public static final int THIRTY_SECONDS = 30;
    public static final int SIXTY_SECONDS  = 60;

    // Milliseconds
    public static final int THREE_HUNDRED_MS  = 300;
    public static final int FIVE_HUNDRED_MS   = 500;
    public static final int ONE_THOUSAND_MS   = 1_000;
    public static final int THREE_THOUSAND_MS = 3_000;
    public static final int FIVE_THOUSAND_MS  = 5_000;
    public static final int TEN_THOUSAND_MS   = 10_000;
    public static final int THIRTY_THOUSAND_MS = 30_000;
}
