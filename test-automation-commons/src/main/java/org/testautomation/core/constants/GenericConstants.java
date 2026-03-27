package org.testautomation.core.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GenericConstants {
    // HTTP client timeout param keys
    public static final String HTTP_CONNECTION_TIMEOUT = "http.connection.timeout";
    public static final String HTTP_SOCKET_TIMEOUT     = "http.socket.timeout";

    // Playwright viewport defaults
    public static final int VIEWPORT_WIDTH  = 1820;
    public static final int VIEWPORT_HEIGHT = 920;

    // Common string tokens
    public static final String TODAY     = "today";
    public static final String YESTERDAY = "yesterday";
    public static final String TOMORROW  = "tomorrow";
    public static final String DESCEND   = "descend";
    public static final String ASCEND    = "ascend";
}
