package org.testautomation.core.context;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Thread-local context for data that must survive across scenario boundaries within the same feature.
 * Each parallel thread gets its own isolated map — no cross-thread contamination.
 *
 * <p>Call {@link #remove()} in an {@code @After} hook to prevent memory leaks.</p>
 *
 * @see ScenarioContext for the preferred scenario-scoped alternative
 */
@Component
public class FeatureContext {

    private final ThreadLocal<Map<String, Object>> context = ThreadLocal.withInitial(HashMap::new);

    public void set(String key, Object value) {
        context.get().put(key, value);
    }

    public <T> T get(String key, Class<T> clazz) {
        return clazz.cast(context.get().get(key));
    }

    public boolean contains(String key) {
        return context.get().containsKey(key);
    }

    /** Clears all entries for the current thread — keeps the map alive. */
    public void clear() {
        context.get().clear();
    }

    /** Removes the context map for the current thread entirely — call at scenario end to avoid leaks. */
    public void remove() {
        context.remove();
    }
}
