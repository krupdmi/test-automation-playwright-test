package org.testautomation.core.ui;

import com.microsoft.playwright.*;
import io.cucumber.spring.ScenarioScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.testautomation.core.constants.GenericConstants;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Creates and manages Playwright browser instances per scenario thread.
 *
 * <p>All tracking maps are keyed by thread ID — safe for parallel execution.
 * Call {@link #cleanupThreadResources()} from an {@code @After} hook.</p>
 *
 * <h3>Configuration properties</h3>
 * <ul>
 *   <li>{@code browser.headless} — default {@code true}</li>
 *   <li>{@code browser.debug.base.port} — default {@code 9222}</li>
 *   <li>{@code browser.blocked.urls} — comma-separated URL globs to abort (default: empty)</li>
 * </ul>
 */
@Configuration
@Slf4j
public class PlaywrightInstanceProvider {

    @Value("${browser.headless:true}")
    private boolean headless;

    @Value("${browser.debug.base.port:9222}")
    private int debugBasePort;

    /**
     * Comma-separated URL glob patterns whose requests will be aborted.
     * Example: {@code browser.blocked.urls=**\/api\/popups\/**,**\/tracking\/**}
     */
    @Value("${browser.blocked.urls:}")
    private List<String> blockedUrls;

    private final PlaywrightCurlCaptureService curlCaptureService;

    private static final ConcurrentHashMap<Long, Playwright>     threadToPlaywright = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, Browser>        threadToBrowser    = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, BrowserContext> threadToBrowserCtx = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, Integer>        threadToPort       = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, Path>           threadToUserDir    = new ConcurrentHashMap<>();
    private static final AtomicInteger                           portCounter        = new AtomicInteger(0);

    public PlaywrightInstanceProvider(PlaywrightCurlCaptureService curlCaptureService) {
        this.curlCaptureService = curlCaptureService;
    }

    // -------------------------------------------------------------------------
    // @ScenarioScope beans — one fresh instance per Cucumber scenario
    // -------------------------------------------------------------------------

    @Bean
    @ScenarioScope
    public Playwright playwright() {
        long tid = tid();
        Playwright pw = Playwright.create();
        threadToPlaywright.put(tid, pw);
        log.info("[Playwright] tid={} — Playwright created", tid);
        return pw;
    }

    @Bean
    @ScenarioScope
    public Browser browser(Playwright playwright) {
        long tid = tid();
        closeExistingBrowser(tid);
        playwright.selectors().setTestIdAttribute("data-testid");
        int port = threadToPort.computeIfAbsent(tid, id -> debugBasePort + portCounter.getAndIncrement());
        Browser b = launchBrowser(playwright, port);
        threadToBrowser.put(tid, b);
        log.info("[Playwright] tid={} — Browser launched (headless={}, port={})", tid, headless, port);
        return b;
    }

    @Bean
    @ScenarioScope
    public BrowserContext browserContext(Browser browser) {
        long tid = tid();
        BrowserContext ctx = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(GenericConstants.VIEWPORT_WIDTH, GenericConstants.VIEWPORT_HEIGHT)
                .setIgnoreHTTPSErrors(true)
                .setJavaScriptEnabled(true)
                .setBypassCSP(true));
        blockUrls(ctx);
        curlCaptureService.setupRequestInterception(ctx);
        threadToBrowserCtx.put(tid, ctx);
        log.info("[Playwright] tid={} — BrowserContext created", tid);
        return ctx;
    }

    @Bean
    @ScenarioScope
    @Primary
    public Page page(BrowserContext browserContext) {
        long tid = tid();
        Page page = browserContext.newPage();
        page.onDialog(d -> {
            log.warn("[Playwright] tid={} — dismissing dialog: {}", tid, d.message());
            d.dismiss();
        });
        page.onRequestFailed(r ->
                log.warn("[Playwright] tid={} — request failed: {} {}", tid, r.url(), r.failure()));
        log.info("[Playwright] tid={} — Page created", tid);
        return page;
    }

    @Bean
    @ScenarioScope
    public PageFactory pageFactory(BrowserContext browserContext) {
        return new PageFactory(browserContext);
    }

    // -------------------------------------------------------------------------
    // Cleanup
    // -------------------------------------------------------------------------

    /** Release all Playwright resources for the current thread. Call from {@code @After}. */
    public static void cleanupThreadResources() {
        long tid = tid();
        log.info("[Playwright] tid={} — cleanup start", tid);
        closeQuietly(threadToBrowserCtx.remove(tid));
        closeBrowserQuietly(threadToBrowser.remove(tid));
        closeQuietly(threadToPlaywright.remove(tid));
        threadToPort.remove(tid);
        Path dir = threadToUserDir.remove(tid);
        if (dir != null) dir.toFile().deleteOnExit();
        log.info("[Playwright] tid={} — cleanup done", tid);
    }

    /** Close every tracked resource across all threads — use in shutdown hooks only. */
    public static void emergencyCleanup() {
        log.warn("[Playwright] Emergency cleanup");
        threadToBrowserCtx.values().forEach(PlaywrightInstanceProvider::closeQuietly);
        threadToBrowserCtx.clear();
        threadToBrowser.values().forEach(PlaywrightInstanceProvider::closeBrowserQuietly);
        threadToBrowser.clear();
        threadToPlaywright.values().forEach(PlaywrightInstanceProvider::closeQuietly);
        threadToPlaywright.clear();
        threadToPort.clear();
        threadToUserDir.clear();
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private Browser launchBrowser(Playwright playwright, int debugPort) {
        BrowserType.LaunchOptions opts = new BrowserType.LaunchOptions()
                .setHeadless(headless)
                .setArgs(buildArgs(debugPort))
                .setTimeout(30_000);
        if (headless) opts.setChannel("chromium");
        return playwright.chromium().launch(opts);
    }

    private List<String> buildArgs(int debugPort) {
        List<String> args = new ArrayList<>();
        if (!headless) args.add("--remote-debugging-port=" + debugPort);
        args.add("--no-sandbox");
        args.add("--disable-dev-shm-usage");
        args.add("--disable-background-timer-throttling");
        args.add("--disable-backgrounding-occluded-windows");
        args.add("--disable-renderer-backgrounding");
        args.add("--disable-features=TranslateUI,VizDisplayCompositor");
        args.add("--disable-extensions");
        args.add("--remote-allow-origins=*");
        args.add("--disable-web-security");
        args.add("--disable-blink-features=AutomationControlled");
        args.add("--no-first-run");
        args.add("--no-default-browser-check");
        if (headless) {
            args.add("--disable-gpu");
            args.add("--disable-software-rasterizer");
        }
        return args;
    }

    private void blockUrls(BrowserContext ctx) {
        if (blockedUrls == null) return;
        blockedUrls.stream().map(String::trim).filter(p -> !p.isBlank()).forEach(pattern ->
                ctx.route(pattern, route -> {
                    log.debug("[Playwright] Blocked: {}", route.request().url());
                    route.abort();
                }));
    }

    private void closeExistingBrowser(long tid) {
        Browser existing = threadToBrowser.get(tid);
        if (existing != null && existing.isConnected()) {
            log.warn("[Playwright] tid={} — closing stale browser before new one", tid);
            closeBrowserQuietly(existing);
            threadToBrowser.remove(tid);
        }
    }

    private static void closeQuietly(AutoCloseable resource) {
        if (resource != null) {
            try { resource.close(); } catch (Exception e) {
                log.warn("[Playwright] Close error: {}", e.getMessage());
            }
        }
    }

    private static void closeBrowserQuietly(Browser b) {
        if (b != null && b.isConnected()) {
            try { b.close(); } catch (Exception e) {
                log.warn("[Playwright] Browser close error: {}", e.getMessage());
            }
        }
    }

    private static long tid() {
        return Thread.currentThread().getId();
    }
}
