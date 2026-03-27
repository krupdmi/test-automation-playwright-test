package org.testautomation.core.ui;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import io.cucumber.spring.ScenarioScope;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Manages multiple Playwright {@link Page} instances within a single scenario.
 * Tracks pages by a {@link PageType} enum key and supports auto-cleanup of secondary pages.
 *
 * <p>Extend {@link PageType} in your project to add application-specific page identifiers.</p>
 */
@Component
@ScenarioScope
public class PageFactory {

    private final BrowserContext browserContext;

    @Getter
    private final Map<PageType, Page> managedPages = new EnumMap<>(PageType.class);
    private final Set<PageType> autoCloseable = new HashSet<>();

    public PageFactory(BrowserContext browserContext) {
        this.browserContext = browserContext;
    }

    /**
     * Creates a new page triggered by {@code action} (e.g. clicking a link that opens a new tab).
     * Pass {@code null} for {@code action} to open a blank new page directly.
     */
    public Page createPage(PageType type, Runnable action) {
        return createPage(type, action, false);
    }

    public Page createPage(PageType type, Runnable action, boolean autoClose) {
        Page page = action != null
                ? browserContext.waitForPage(action)
                : browserContext.newPage();
        managedPages.put(type, page);
        if (autoClose) autoCloseable.add(type);
        page.bringToFront();
        page.waitForLoadState();
        return page;
    }

    public Page getPage(PageType type) {
        return managedPages.get(type);
    }

    public boolean hasPage(PageType type) {
        Page page = managedPages.get(type);
        return page != null && !page.isClosed();
    }

    public void closePage(PageType type) {
        Page page = managedPages.remove(type);
        if (page != null && !page.isClosed()) page.close();
        autoCloseable.remove(type);
    }

    /** Returns the main page, falling back to the first open page in the context. */
    public Page getMainPage() {
        Page main = managedPages.get(PageType.MAIN);
        if ((main == null || main.isClosed()) && !browserContext.pages().isEmpty()) {
            main = browserContext.pages().get(0);
            managedPages.put(PageType.MAIN, main);
        }
        return main;
    }

    public void closeAutoCloseablePages() {
        new HashSet<>(autoCloseable).forEach(this::closePage);
    }

    @PreDestroy
    public void cleanup() {
        managedPages.values().stream().filter(p -> !p.isClosed()).forEach(Page::close);
        managedPages.clear();
        autoCloseable.clear();
    }

    // -------------------------------------------------------------------------
    // Extend this enum in your project for application-specific page types
    // -------------------------------------------------------------------------
    public enum PageType {
        MAIN,
        SECONDARY,
        POPUP
    }
}
