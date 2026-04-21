package org.testautomation.commons.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Single, shared Cucumber-Spring context configuration.
 * Both {@code test-automation-e2e-api} and {@code test-automation-e2e-ui} pick this up
 * from the classpath — no per-module duplicate is needed, which avoids the
 * "multiple @CucumberContextConfiguration" conflict.
 */
@CucumberContextConfiguration
@SpringBootTest(classes = TestConfig.class)
public class CucumberSpringConfiguration {
    // This class serves as the unified Spring context configuration for Cucumber tests.
    // Both UI and API modules will use this shared configuration.
}
