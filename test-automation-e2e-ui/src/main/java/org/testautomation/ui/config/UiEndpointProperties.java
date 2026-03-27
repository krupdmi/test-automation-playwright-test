package org.testautomation.ui.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Binds all {@code ui.base-url.*} properties from {@code application-endpoints.properties}.
 *
 * <p>Example:
 * <pre>
 *   ui.base-url.app=https://app.example.com
 *   ui.base-url.admin=https://admin.example.com
 * </pre>
 */
@Configuration
@ConfigurationProperties(prefix = "ui.base-url")
@Getter
@Setter
public class UiEndpointProperties {

    /** Base URL of the main application under test. */
    private String app;

    /** Base URL of the admin / back-office portal. */
    private String admin;
}
