package org.testautomation.commons.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Keys used with {@link org.testautomation.core.context.ScenarioContext}.
 * Add project-specific keys here as your test suite grows.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ScenarioContextConstants {

    // Authentication
    public static final String CURRENT_USERNAME = "currentUsername";
    public static final String SCENARIO_USER_TOKEN = "scenarioUserToken";

    // Common request/response
    public static final String REQUEST_BODY = "requestBody";
    public static final String RESPONSE_BODY = "responseBody";
    public static final String RESPONSE_STATUS = "responseStatus";
    public static final String LAST_API_ERROR = "lastApiError";

    // Resource identifiers
    public static final String ENTITY_ID = "entityId";
    public static final String TRANSACTION_ID = "transactionId";

    // Pagination
    public static final String PAGE_NUMBER = "pageNumber";
    public static final String PAGE_SIZE = "pageSize";
    public static final String TOTAL_ELEMENTS = "totalElements";

    // Date range
    public static final String FROM_DATE = "fromDate";
    public static final String TO_DATE = "toDate";
    public static final String CURRENT_DATE_TIME = "currentDateTime";

    // Comparison helpers
    public static final String EXPECTED_RESULT = "expectedResult";
    public static final String ACTUAL_RESULT = "actualResult";
}
