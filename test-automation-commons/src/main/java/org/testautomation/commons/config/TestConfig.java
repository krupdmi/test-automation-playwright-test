package org.testautomation.commons.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Root Spring Boot configuration for the test application context.
 * Scans both {@code org.testautomation.core} and {@code org.testautomation.commons}
 * so infrastructure beans (Playwright, BaseApiClient, DB drivers) are always available.
 */
@SpringBootApplication
@ComponentScan(basePackages = {
        "org.testautomation.core",
        "org.testautomation.commons"
})
@EntityScan(basePackages = "org.testautomation.commons.entities")
@EnableJpaRepositories(basePackages = "org.testautomation.commons.repository")
public class TestConfig {
}
