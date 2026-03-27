package org.testautomation.commons.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Holds parallel execution settings and exposes them to both the user pool
 * and any component that needs to know the configured thread count.
 */
@Getter
@Component
@Slf4j
public class ExecutionConfig {

    @Value("${parallel.threads:4}")
    private int threadCount;

    @Value("${jenkins.agent.name:local}")
    private String agentName;

    @PostConstruct
    public void init() {
        // Propagate to system property so Maven Surefire can read it
        System.setProperty("parallel.threads", String.valueOf(threadCount));
        log.info("ExecutionConfig — agent={}, threads={}", agentName, threadCount);
    }
}
