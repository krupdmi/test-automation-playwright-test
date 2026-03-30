# Code Style Documentation

This document describes all code style settings applied in the project. The configuration consists of two files:

- **`config/CodeStyle.xml`** – IntelliJ IDEA Java formatting (import via File → Import Settings)
- **`.editorconfig`** – Cross-editor settings (IntelliJ, VS Code, etc.)

---

## Table of Contents

1. [EditorConfig (.editorconfig)](#1-editorconfig-editorconfig)
2. [General Settings (CodeStyle.xml)](#2-general-settings-codestylexml)
3. [Java Code Style Settings](#3-java-code-style-settings)
4. [Wrapping and Braces](#4-wrapping-and-braces)
5. [Spaces and Operators](#5-spaces-and-operators)
6. [How to Apply](#6-how-to-apply)

---

## 1. EditorConfig (.editorconfig)

EditorConfig is a standard format supported by most editors. Settings apply automatically when you open files.

### `root = true`

**What it does:** Stops searching for `.editorconfig` in parent directories. This file applies only to the current project.

---

### `[*]` – All Files

| Option | Value | Description |
|--------|-------|-------------|
| `charset` | `utf-8` | File encoding |
| `end_of_line` | `lf` | Line ending: LF (`\n`), not CRLF (`\r\n`) |
| `insert_final_newline` | `true` | Ensures file ends with a newline character |
| `trim_trailing_whitespace` | `true` | Removes spaces at end of lines on save |

**Why `insert_final_newline`?** POSIX defines a text file as ending with a newline. Git warns without it. Some tools expect it.

**Why `trim_trailing_whitespace`?** Trailing spaces cause noisy Git diffs and serve no purpose.

---

### `[*.{java,kt}]` – Java and Kotlin

| Option | Value | Description |
|--------|-------|-------------|
| `indent_style` | `space` | Use spaces, not tabs |
| `indent_size` | `4` | 4 spaces per indent level |

---

### `[*.{xml,json,yml,yaml,properties}]` – Config Files

| Option | Value | Description |
|--------|-------|-------------|
| `indent_size` | `4` | 4 spaces for indentation |

---

### `[*.md]` – Markdown

| Option | Value | Description |
|--------|-------|-------------|
| `trim_trailing_whitespace` | `false` | Keep trailing spaces (two spaces = line break in Markdown) |

---

## 2. General Settings (CodeStyle.xml)

### `AUTODETECT_INDENTS` = false

**What it does:** Disables automatic detection of indentation from file content.

**Why:** Ensures consistent indentation regardless of existing file style.

---

### `INDENT_SIZE` = 4

**What it does:** Number of spaces for each indentation level.

**Example:**
```java
public class Example {      // 0 spaces
    void method() {         // 4 spaces
        if (x) {            // 8 spaces
            doIt();         // 12 spaces
        }
    }
}
```

---

### `TAB_SIZE` = 4

**What it does:** One Tab character equals 4 spaces (when tabs are used).

---

### `USE_TAB_CHARACTER` = false

**What it does:** Use spaces instead of Tab characters.

**Why:** Spaces render consistently across editors and tools; tabs can vary.

---

### `CONTINUATION_INDENT_SIZE` = 8

**What it does:** Extra indentation for wrapped/continuation lines.

**Example:**
```java
String result = response.then()
        .statusCode(200)
        .extract()
        .body()
        .as(String.class);
//        ↑ 8 spaces from line start
```

---

## 3. Java Code Style Settings

### `RECORD_COMPONENTS_WRAP` = 5 (Chop down if long)

**What it does:** When a record declaration is wrapped, each component goes on its own line.

**Example:**
```java
public record Transaction(
        String id,
        String status,
        BigDecimal amount
) {}
```

---

### `CLASS_COUNT_TO_USE_IMPORT_ON_DEMAND` = 99

**What it does:** Effectively disables wildcard imports like `import java.util.*`. Use explicit imports.

**Why:** Clear origin of each class, avoids name conflicts, cleaner diffs.

**Example:**
```java
// With 99 – explicit imports
import java.util.List;
import java.util.Map;

// With 5 – wildcard when 5+ from same package
// import java.util.*;
```

---

### `NAMES_COUNT_TO_USE_IMPORT_ON_DEMAND` = 99

**What it does:** Same for static imports – no `import static X.*`.

**Example:**
```java
// With 99 – explicit
import static org.testautomation.commons.constants.GenericConstants.STATUS;

// With 3 – wildcard
// import static org.testautomation.commons.constants.GenericConstants.*;
```

---

## 4. Wrapping and Braces

### `RIGHT_MARGIN` = 140

**What it does:** Maximum line length in characters. Lines longer than this are wrapped.

**Example:**
```java
// Under 140 – stays on one line
String x = "short";

// Over 140 – gets wrapped
verityTransactionDataUtil.verifyTransactionData(expectedData, actualData,
        Set.of("userAgent"),
        Map.of("date", Duration.ofSeconds(60)));
```

---

### `KEEP_BLANK_LINES_IN_CODE` = 1

**What it does:** Maximum 1 blank line between logical blocks.

**Example:**
```java
public void method() {
    doFirst();

    doSecond();   // 1 blank line between blocks

    doThird();
}
```

---

### `KEEP_BLANK_LINES_BEFORE_RBRACE` = 0

**What it does:** No blank lines before closing brace `}`.

**Example:**
```java
public void method() {
    doSomething();
}
```

---

### `ALIGN_MULTILINE_PARAMETERS_IN_CALLS` = true

**What it does:** Aligns parameters vertically when a method call is wrapped.

**Example:**
```java
service.processTransaction(transactionId,
        amount,
        currency,
        status);
```

---

### `ALIGN_MULTILINE_TERNARY_OPERATION` = true

**What it does:** Aligns ternary operator parts when wrapped.

**Example:**
```java
String status = passed
        ? "OK"
        : "FAIL";
```

---

### `CALL_PARAMETERS_WRAP` = 1 (Wrap if long)

**What it does:** Method call parameters wrap only when the line exceeds the margin. First parameter can stay on the same line.

**Values:** 0 = Do not wrap, 1 = Wrap if long, 5 = Chop down (each on new line)

**Example:**
```java
// Short – one line
method(a, b, c);

// Long – wrapped
method(param1,
        param2,
        param3);
```

---

### `METHOD_PARAMETERS_WRAP` = 1 (Wrap if long)

**What it does:** Same for method declaration parameters.

**Example:**
```java
// Short – one line
public void process(String id, int amount) {}

// Long – wrapped
public void process(String id, String name, int amount,
        boolean flag) {}
```

---

### `METHOD_CALL_CHAIN_WRAP` = 1 (Wrap if long)

**What it does:** Chained method calls wrap only when the line exceeds the margin.

**Example:**
```java
// Short – one line
response.jsonPath().getString(field);

// Long – wrapped
response.then()
        .statusCode(200)
        .extract()
        .body()
        .as(MyClass.class);
```

---

### `TERNARY_OPERATION_WRAP` = 1 (Wrap if long)

**What it does:** Ternary operator wraps only when needed.

**Example:**
```java
// Short – one line
String x = a ? "yes" : "no";

// Long – wrapped
String status = condition
        ? computeValue()
        : getDefault();
```

---

### `TERNARY_OPERATION_SIGNS_ON_NEXT_LINE` = true

**What it does:** When ternary is wrapped, `?` and `:` go on separate lines.

**Example:**
```java
String result = passed
        ? "OK"
        : "FAIL";
```

---

### `IF_BRACE_FORCE` = 1 (Always)

**What it does:** Always use braces `{}` in `if` statements.

**Example:**
```java
// Correct
if (condition) {
    doSomething();
}

// Not allowed
// if (condition) doSomething();
```

---

### `WHILE_BRACE_FORCE` = 1 (Always)

**What it does:** Always use braces in `while` loops.

---

### `FOR_BRACE_FORCE` = 1 (Always)

**What it does:** Always use braces in `for` loops.

---

### `DOWHILE_BRACE_FORCE` = 1 (Always)

**What it does:** Always use braces in `do-while` loops.

---

## 5. Spaces and Operators

### `SPACE_BEFORE_IF_PARENTHESES` = false

**What it does:** No space between `if` and `(`.

**Example:**
```java
if (condition) {}   // not: if (condition)
```

---

### `SPACE_BEFORE_METHOD_PARENTHESES` = false

**What it does:** No space between method name and `(` in declarations.

**Example:**
```java
public void method() {}   // not: method ()
```

---

### `SPACE_BEFORE_METHOD_CALL_PARENTHESES` = false

**What it does:** No space between method name and `(` in calls.

**Example:**
```java
obj.method();   // not: obj.method ()
```

---

### `SPACE_AROUND_ASSIGNMENT_OPERATORS` = true

**What it does:** Spaces around `=`, `+=`, etc.

**Example:**
```java
int a = 5;
x += 10;
```

---

### `SPACE_AROUND_EQUALITY_OPERATORS` = true

**What it does:** Spaces around `==`, `!=`, `<=`, `>=`.

**Example:**
```java
if (a == b) {}
if (x != 0) {}
```

---

## 6. How to Apply

### IntelliJ IDEA

1. **Import CodeStyle:** File → Manage IDE Settings → Import Settings → select `config/CodeStyle.xml`
2. Or: File → Settings → Editor → Code Style → ⚙️ → Import Scheme → IntelliJ IDEA code style XML → select `config/CodeStyle.xml`
3. **Format on Save:** File → Settings → Tools → Actions on Save → enable "Reformat code" and "Optimize imports"

### EditorConfig

- Applied automatically by IntelliJ, VS Code, and other editors that support EditorConfig.
- No manual import needed.

### Reformat Shortcut

- **Ctrl+Alt+L** (Windows/Linux) or **Cmd+Option+L** (Mac) – reformat selected code or entire file

---

## Quick Reference Table

| Category | Option | Value | Effect |
|----------|--------|-------|--------|
| Margin | RIGHT_MARGIN | 140 | Max line length |
| Indent | INDENT_SIZE | 4 | Spaces per level |
| Indent | USE_TAB_CHARACTER | false | Use spaces |
| Wrap | CALL_PARAMETERS_WRAP | 1 | Wrap if long |
| Wrap | METHOD_PARAMETERS_WRAP | 1 | Wrap if long |
| Wrap | METHOD_CALL_CHAIN_WRAP | 1 | Wrap if long |
| Braces | IF/WHILE/FOR/DOWHILE_BRACE_FORCE | 1 | Always use {} |
| Imports | CLASS_COUNT_TO_USE_IMPORT_ON_DEMAND | 99 | No wildcards |
| Spaces | SPACE_AROUND_ASSIGNMENT_OPERATORS | true | a = b |
| Spaces | SPACE_BEFORE_IF_PARENTHESES | false | if( |
