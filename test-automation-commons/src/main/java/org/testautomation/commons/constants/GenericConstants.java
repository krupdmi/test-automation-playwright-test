package org.testautomation.commons.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Domain-neutral string/numeric constants shared across both test modules.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GenericConstants {

    // Token keys (used with System properties for s2s flows)
    public static final String USER_TOKEN = "userToken";
    public static final String ADMIN_TOKEN = "adminToken";

    // Common field names
    public static final String STATUS = "status";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String AMOUNT = "amount";
    public static final String CURRENCY = "currency";
    public static final String TIMESTAMP = "timestamp";
    public static final String CONTENT = "content";

    // Common status strings
    public static final String STATUS_OK = "OK";
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_PENDING = "PENDING";

    // Pagination defaults
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int DEFAULT_PAGE_NUMBER = 0;
}
