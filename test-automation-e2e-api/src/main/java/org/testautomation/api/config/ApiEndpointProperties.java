package org.testautomation.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Binds all {@code api.base-url.*} properties from {@code application-endpoints.properties}.
 *
 * <p>Example:
 * <pre>
 *   api.base-url.user-service=https://api.example.com/users
 *   api.base-url.order-service=https://api.example.com/orders
 * </pre>
 */
@Configuration
@ConfigurationProperties(prefix = "api.base-url")
@Getter
@Setter
public class ApiEndpointProperties {

    /** Base URL for the User service. */
    private String userService;

    /** Base URL for the Order service. */
    private String orderService;
}
