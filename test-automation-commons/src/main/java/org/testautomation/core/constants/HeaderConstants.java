package org.testautomation.core.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HeaderConstants {
    public static final String AUTHORIZATION     = "Authorization";
    public static final String BEARER            = "Bearer ";
    public static final String CONTENT_TYPE      = "Content-Type";
    public static final String ACCEPT            = "Accept";
    public static final String X_TRACE_ID        = "x-trace-id";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
    public static final String CONTENT_TYPE_TEXT = "text/plain;charset=UTF-8";
}
