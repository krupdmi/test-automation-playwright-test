package org.testautomation.core.context;

import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Scenario-scoped context for passing data between step definitions.
 * A new instance is created per Cucumber scenario and discarded when the scenario ends.
 * Thread-safe by design — Spring's @ScenarioScope guarantees one instance per scenario thread.
 */
@Component
@ScenarioScope
public class ScenarioContext {

    private final Map<String, Object> context = new HashMap<>();

    public void set(String key, Object value) {
        context.put(key, value);
    }

    public <T> T get(String key, Class<T> clazz) {
        return clazz.cast(context.get(key));
    }

    /**
     * Returns the value for the given key, throwing if absent or null.
     */
    public <T> T getRequired(String key, Class<T> clazz) {
        T value = get(key, clazz);
        if (value == null) {
            throw new IllegalStateException(
                    String.format("Required value for key '%s' is missing in ScenarioContext", key));
        }
        return value;
    }

    public boolean contains(String key) {
        return context.containsKey(key);
    }

    public void clear() {
        context.clear();
    }
}
