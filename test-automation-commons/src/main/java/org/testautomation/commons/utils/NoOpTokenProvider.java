package org.testautomation.commons.utils;

import io.cucumber.java.Scenario;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * No-op {@link TokenProvider} that is active only when no real implementation is registered.
 *
 * <p>This prevents Spring context startup failures in skeleton/demo mode where no auth
 * endpoint is configured. Replace it with a real implementation for actual test runs:</p>
 *
 * <pre>
 * {@code
 * @Component  // <-- this will take precedence and disable the NoOp
 * @Primary
 * public class RealTokenProvider implements TokenProvider { ... }
 * }
 * </pre>
 */
@Slf4j
@Component
@ConditionalOnMissingBean(value = TokenProvider.class, ignored = NoOpTokenProvider.class)
public class NoOpTokenProvider implements TokenProvider {

    @Override
    public String getAuthToken(String username, Scenario scenario) {
        log.warn("NoOpTokenProvider — no real TokenProvider registered. " +
                 "Returning placeholder token for user '{}'. " +
                 "Implement TokenProvider and register it as a @Component for real auth.", username);
        return "placeholder-token-for-" + username;
    }
}
