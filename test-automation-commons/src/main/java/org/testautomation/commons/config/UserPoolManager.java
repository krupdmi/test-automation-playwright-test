package org.testautomation.commons.config;

import io.cucumber.java.Scenario;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.testautomation.core.utils.ScenarioContextHolder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages a pool of test user accounts for parallel scenario execution.
 *
 * <p><b>User pool configuration:</b></p>
 * <ul>
 *   <li>{@code test.users} — comma-separated usernames (e.g. {@code user1,user2,user3})</li>
 *   <li>{@code test.users.password} — shared password for all pool users</li>
 *   <li>{@code test.users.pool.<tag>} — tag-specific user pool (e.g. {@code test.users.pool.admin})</li>
 * </ul>
 *
 * <p>Each scenario acquires one user slot before running and releases it in {@code @After}.
 * Acquisition blocks until a slot is free (up to 5 minutes).</p>
 */
@Component
@Slf4j
public class UserPoolManager {

    @Value("${test.users:}")
    private String usersConfig;

    @Value("${test.users.password:changeme}")
    private String defaultPassword;

    @Autowired(required = false)
    private ExecutionConfig executionConfig;

    @Autowired(required = false)
    private Environment environment;

    private String[] userPool;
    private Semaphore semaphore;

    private final Map<String, UserSession> activeSessions = new ConcurrentHashMap<>();
    private final AtomicInteger index = new AtomicInteger(0);

    @PostConstruct
    public void init() {
        userPool = usersConfig.isBlank()
                   ? new String[]{"default_user"}
                   : usersConfig.split(",");

        int slots = executionConfig != null
                    ? executionConfig.getThreadCount()
                    : Integer.parseInt(System.getProperty("parallel.threads", "1"));

        semaphore = new Semaphore(Math.max(1, slots));
        log.info("UserPoolManager — pool={} users, semaphore={} slots", userPool.length, slots);
    }

    public UserSession acquireUser() {
        try {
            if(!semaphore.tryAcquire(5, TimeUnit.MINUTES)) {
                throw new IllegalStateException("Timed out waiting for a free user slot after 5 minutes");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for user slot", e);
        }

        Scenario scenario = ScenarioContextHolder.getScenario();
        if(scenario == null) throw new IllegalStateException("ScenarioContextHolder has no Scenario — call from @Before only");

        String username = nextUser(scenario).trim();
        UserSession session = new UserSession(username, defaultPassword);
        activeSessions.put(scenario.getId(), session);

        log.info("Scenario [{}] acquired user '{}' (thread={})",
                 scenario.getId(), username, Thread.currentThread().getName());
        return session;
    }

    public void releaseUser(Scenario scenario) {
        if(scenario == null) return;
        UserSession session = activeSessions.remove(scenario.getId());
        if(session != null) {
            semaphore.release();
            log.info("Scenario [{}] released user '{}' (thread={})",
                     scenario.getId(), session.getUsername(), Thread.currentThread().getName());
        }
    }

    public UserSession getCurrentSession() {
        Scenario scenario = ScenarioContextHolder.getScenario();
        if(scenario == null) throw new IllegalStateException("No active scenario in ScenarioContextHolder");
        UserSession session = activeSessions.get(scenario.getId());
        if(session == null) throw new IllegalStateException("No active user session for scenario: " + scenario.getId());
        return session;
    }

    /**
     * Returns the appropriate pool for the scenario — tag-specific if configured, else default.
     * Tag-specific pools are defined as: {@code test.users.pool.<tagName>=user1,user2}
     */
    private String nextUser(Scenario scenario) {
        if(environment != null && scenario != null) {
            for (String tag : scenario.getSourceTagNames()) {
                String key = "test.users.pool." + tag.replace("@", "");
                String tagPool = environment.getProperty(key);
                if(tagPool != null && !tagPool.isBlank()) {
                    String[] pool = tagPool.split(",");
                    return pool[index.getAndUpdate(i -> (i + 1) % pool.length)].trim();
                }
            }
        }
        return userPool[index.getAndUpdate(i -> (i + 1) % userPool.length)];
    }

    @Getter
    @RequiredArgsConstructor
    public static class UserSession {
        private final String username;
        private final String password;
        private final long startedAt = System.currentTimeMillis();

        public long elapsedMinutes() {
            return (System.currentTimeMillis() - startedAt) / 60_000;
        }
    }
}
