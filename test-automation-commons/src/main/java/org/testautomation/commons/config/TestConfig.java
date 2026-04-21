package org.testautomation.commons.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Unified test configuration for both UI and API test modules.
 * Playwright beans are conditionally loaded only when
 * PlaywrightInstanceProvider is on the classpath
 */
@Configuration
@EnableAutoConfiguration
@EntityScan("org.testautomation.commons.entities")
@EnableJpaRepositories("org.testautomation.commons.repository")
@ComponentScan(basePackages = {
    "org.testautomation.core",
    "org.testautomation.commons",
    "org.testautomation.ui",
    "org.testautomation.api"
})
public class TestConfig {

    @Configuration
    @ConditionalOnClass(name = "org.testautomation.core.ui.PlaywrightInstanceProvider")
    @Import(org.testautomation.core.ui.PlaywrightInstanceProvider.class)
    static class UIConfiguration {
    }
}
