# test-automation-playwright-test

BDD automation framework skeleton built on **Java 21 · Spring Boot 3 · Cucumber 7 · JUnit 5 · REST Assured · Playwright**.

---

## Module map

```
test-automation-playwright-test/        ← root POM (version management, plugin config)
├── test-automation-commons/            ← infra + shared test-domain layer
│   └── src/main/java/org/testautomation/
│       ├── core/                       ← pure infrastructure (no domain knowledge)
│       │   ├── api/BaseApiClient       ← REST Assured spec factory
│       │   ├── context/                ← ScenarioContext (@ScenarioScope), FeatureContext (ThreadLocal)
│       │   ├── db/                     ← DbDriver (JDBC), RedisDriver (Jedis + RedisTemplate)
│       │   ├── ui/                     ← PlaywrightInstanceProvider, PageFactory, interceptors
│       │   ├── utils/                  ← AllureRestAssuredFilter, ReportManager, ScreenshotUtil
│       │   └── constants/              ← GenericConstants, HeaderConstants, DateTimeFormatConstants
│       └── commons/                    ← test-domain shared layer
│           ├── config/                 ← CucumberSpringConfiguration, TestConfig, UserPoolManager
│           ├── builders/               ← UserBuilder, OrderBuilder (fluent, fake-data pre-filled)
│           ├── constants/              ← ScenarioContextConstants, GenericConstants, time constants
│           ├── dbservices/             ← UserDbService
│           ├── entities/               ← UserEntity (JPA, UUID PK)
│           ├── enums/                  ← Channel, Currency
│           ├── repository/             ← UserRepository (Spring Data JPA)
│           ├── stepdefinitions/        ← CommonStepDefinitionsAPI (shared steps)
│           └── utils/                  ← TokenHolder, RetryHelper, ComparatorUtils, FakeDataHelper…
├── test-automation-e2e-api/            ← API test module
│   └── src/
│       ├── main/java/org/testautomation/api/
│       │   ├── config/ApiEndpointProperties
│       │   └── models/be/{user,order}/ ← request/response POJOs
│       └── test/java/org/testautomation/api/
│           ├── apiclients/             ← UserApiClient, OrderApiClient
│           ├── hooks/Hooks             ← @Before/@After lifecycle
│           ├── listeners/AllureStepLogger
│           ├── runners/UniversalTestRunner
│           └── stepdefinitions/{user,order}/
└── test-automation-e2e-ui/             ← UI test module
    └── src/
        ├── main/java/org/testautomation/ui/
        │   ├── config/UiEndpointProperties
        │   ├── pages/                  ← BasePage, LoginPage, DashboardPage
        │   └── utils/                  ← Actions, WaitActions, AlertHandler
        └── test/java/org/testautomation/ui/
            ├── hooks/Hooks             ← @Before/@After + screenshot on failure
            ├── listeners/              ← AllureStepLogger, BrokenToFailedLifecycleListener
            ├── runners/UniversalTestRunner
            └── stepdefinitions/{login,dashboard}/
```

---

## Prerequisites

| Tool | Version |
|------|---------|
| Java | 21 |
| Maven | 3.9+ |
| Docker | (optional — for local DB/Redis) |
| Playwright browsers | installed via `mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install chromium"` |

---

## Quick start

```bash
# 1. Clone
git clone https://github.com/krupdmi/test-automation-playwright-test.git
cd test-automation-playwright-test

# 2. Copy and populate local config (do NOT commit real values)
cp test-automation-e2e-api/src/test/resources/application-local.properties \
   test-automation-e2e-api/src/test/resources/application-local.properties.bak
# Edit application-local.properties with your local URLs and credentials

# 3. Build (skips tests)
mvn clean install -DskipTests

# 4. Run API smoke tests
mvn test -pl test-automation-e2e-api -Dcucumber.filter.tags="@smoke"

# 5. Run UI smoke tests
mvn test -pl test-automation-e2e-ui -Dcucumber.filter.tags="@smoke"

# 6. Run everything
mvn test

# 7. Generate Allure report
mvn allure:report
# Open allure-report/index.html
```

---

## Environment configuration

Each test module has three layered property files:

| File | Purpose | Committed? |
|------|---------|-----------|
| `application.properties` | Active profiles, framework defaults | ✅ Yes |
| `application-endpoints.properties` | URL templates (all values commented out) | ✅ Yes |
| `application-local.properties` | Your local overrides | ✅ Yes (empty template) |

**To target a specific environment at runtime:**

```bash
mvn test -pl test-automation-e2e-api \
  -Dspring.profiles.active=dev,endpoints \
  -Dparallel.threads=4
```

Create `application-dev.properties` / `application-int.properties` / `application-uat.properties` alongside the existing files and populate them with CI secrets via Jenkins credentials binding — never hard-code them.

---

## Tag conventions

| Tag | Meaning |
|-----|---------|
| `@api` | Required on all API scenarios — activates API hooks |
| `@ui` | Required on all UI scenarios — activates UI hooks + Playwright lifecycle |
| `@smoke` | Fast, critical-path scenarios run on every pipeline build |
| `@regression` | Full regression suite |
| `@userToken` | Scenario needs an authenticated user token — triggers token Before hook |
| `@negative` | Negative / error-path scenarios |

Filter by tag at runtime:

```bash
# Single tag
mvn test -Dcucumber.filter.tags="@smoke"

# Combination
mvn test -Dcucumber.filter.tags="@smoke and @api"

# Exclusion
mvn test -Dcucumber.filter.tags="@regression and not @wip"
```

---

## Parallel execution

Parallelism is controlled by a single property flowing through the stack:

```
junit-platform.properties          ← sets Cucumber's fixed thread count
    ↑
ExecutionConfig (@PostConstruct)   ← reads parallel.threads, sets System property
    ↑
UserPoolManager                    ← semaphore slots = parallel.threads
    ↑
PlaywrightInstanceProvider         ← one Browser/BrowserContext/Page per thread
```

Override at runtime: `-Dparallel.threads=8`

**Thread safety guarantees in this skeleton:**
- `ScenarioContext` is `@ScenarioScope` — Spring creates one instance per scenario thread
- `TokenHolder` and `FeatureContext` use `ThreadLocal` — no cross-thread leakage
- `UserPoolManager` uses `ConcurrentHashMap` keyed by `scenario.getId()` — safe across async scenarios
- `PlaywrightInstanceProvider` tracks browser/context/playwright per thread ID in `ConcurrentHashMap`

---

## How to add a new test module

If your project grows to need a third module (e.g. `test-automation-e2e-mobile`):

1. Create the Maven module directory and `pom.xml` inheriting from the root, depending on `test-automation-commons`
2. Add `<module>test-automation-e2e-mobile</module>` to the root `pom.xml`
3. Create a `Hooks` class with `@Before(value = "@mobile")` / `@After(value = "@mobile")`
4. Create a `UniversalTestRunner` with the correct `GLUE_PROPERTY_NAME` pointing to your hooks and step definitions
5. Add `application.properties` + `application-endpoints.properties` + `application-local.properties`
6. Tag all feature files with `@mobile`

---

## How to add a new API endpoint

1. Add the base URL to `ApiEndpointProperties` and `application-endpoints.properties`
2. Create `YourApiClient` in `test-automation-e2e-api/src/test/java/.../apiclients/` extending nothing — inject `BaseApiClient` and `ApiEndpointProperties`
3. Create request/response POJOs under `models/be/yourservice/`
4. Create step definitions under `stepdefinitions/yourservice/`
5. Create feature file under `src/test/resources/.../features/yourservice/`

---

## How to add a new UI page

1. Create `YourPage extends BasePage` in `test-automation-e2e-ui/src/main/java/.../pages/`
2. Define selectors as private constants using `data-testid` attributes
3. Implement action methods (navigate, click, fill) and assertion helpers (isDisplayed, getText)
4. Instantiate the page inside step definitions: `new YourPage(page)` — do NOT make pages Spring beans
5. Create step definitions under `stepdefinitions/yourfeature/`
6. Create feature file under `src/test/resources/.../features/yourfeature/`

---

## Allure reporting

Allure results from both modules are aggregated at the root level during `mvn verify`:

```bash
# Generate report after a test run
mvn allure:report

# Serve interactively
mvn allure:serve
```

Each scenario automatically gets:
- Step-level pass/fail from `AllureStepLogger`
- cURL commands and API response bodies attached (API module)
- Screenshot on failure attached (UI module)
- Broken → Failed remapping so all failures appear in one bucket (UI module)

---

## Project conventions

- **Package boundary**: `org.testautomation.core.*` — zero domain knowledge. If you find yourself importing a domain model here, move the code to `commons`
- **Page objects are not Spring beans** — instantiate them with `new` inside step definitions to avoid stale `Page` references across scenarios
- **Never use `System.setProperty` for tokens** — use `TokenHolder` (ThreadLocal) instead
- **`ScenarioContext` over instance fields** — step definitions are Spring singletons; instance fields break parallel runs
- **One feature file per domain concept** — keep scenarios focused; use tags for cross-cutting grouping
