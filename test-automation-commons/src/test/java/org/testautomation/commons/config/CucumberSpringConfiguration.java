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
}
