# Monefy Mobile App — Test Automation

End-to-end test automation suite for the [Monefy](https://monefy.me/) personal finance Android application, built with **Appium** and **TestNG** on the Android platform.

---

## Table of Contents

- [About the Project](#about-the-project)
- [Test Scenarios](#test-scenarios)
- [Test Architecture](#test-architecture)
- [Prerequisites](#prerequisites)
- [Setup & Configuration](#setup--configuration)
- [Running the Tests](#running-the-tests)
- [Project Structure](#project-structure)
- [Bug Fixes Applied](#bug-fixes-applied)
- [Known Limitations](#known-limitations)

---

## About the Project

Monefy is a money management app that lets users record income and expenses across categories, then visualise spending through charts and breakdowns. This project automates a core set of functional scenarios against the **Monefy Lite** APK (`com.monefy.app.lite`) running on an Android emulator.

**Why Appium?**

- Tests run against the same APK published on the Play Store — no source-code access required.
- Tests can be written in any JVM language with any framework.
- Compliant with the W3C WebDriver specification.
- Open-source with broad community support.
- Supports both native and hybrid apps on iOS and Android from a single API.

---

## Test Scenarios

Tests are executed in the following order (each scenario builds on the previous app state):

| # | Test Name | Action | Assertion |
|---|-----------|--------|-----------|
| 1 | **Add Income** | Enter `$150.00` income → select Deposits category | Balance displays `Balance $150.00` |
| 2 | **Add Bills Expense** | Enter `$50.00` Bills expense | Balance displays `Balance $100.00` |
| 3 | **Add Food Expense** | Enter `$80.00` Food expense | Balance displays `Balance $20.00` |
| 4 | **Verify Food Percentage** | Read food expense and total expenses | Food percentage label matches `(food / total) × 100%` |
| 5 | **Delete Food Expense** | Navigate to transactions list → delete Food entry | Balance returns to `Balance $100.00` |

### Test Prioritization Rationale

Income must be recorded first (priority 1) to avoid a zero-balance rejection on the first expense entry. Each subsequent test depends on the cumulative app state left by the previous test, so strict priority ordering is enforced via TestNG `priority` attributes.

---

## Test Architecture

```
MonefyTest.java          ← TestNG entry point (@BeforeTest / @Test / @AfterTest)
  │
  ├── AddIncomeAndAssert.java          ← Income flow + balance assertion
  ├── AddExpenseBillsAndAssert.java    ← Bills expense flow + balance assertion
  ├── AddExpenseFoodAndAssert.java     ← Food expense flow + balance assertion
  ├── PercentageOfFoodExpense.java     ← Percentage calculation + label assertion
  └── DeleteFoodExpenseAndAssert.java  ← Delete transaction + balance assertion
```

Each action class is a stateless utility holding a single `public static` method, keeping the test runner (`MonefyTest`) free of UI logic.

---

## Prerequisites

| Tool | Version | Notes |
|------|---------|-------|
| Android Studio | 4.1.1+ | IDE + SDK Manager |
| Android SDK | API 28 (Android 9.0 Pie) | Set in SDK Manager |
| Android Emulator | 30.2.6+ | Pixel 4 · 1080×2220 · xxhdpi |
| Appium Server | 1.18.3 | Must be running before tests |
| Java | 1.8 | Set as `sourceCompatibility` in Gradle |
| Gradle | 6.5 | Managed by the Gradle wrapper |

---

## Setup & Configuration

### 1. Clone the repository

```bash
git clone https://github.com/<your-username>/monefy_mobile_app.git
cd monefy_mobile_app/monefy_app_automation
```

### 2. Configure the emulator in Android Studio

1. Open **SDK Manager** → **SDK Platforms** → install **Android 9.0 (Pie) API 28**.
2. Open **SDK Manager** → **SDK Tools** → verify:
   - Android Emulator ≥ 30.2.6
   - Android SDK Build-Tools 30.0.0
3. Open **AVD Manager** → create a virtual device:
   - Device: **Pixel 4** (5.7", 1080×2220 xxhdpi)
   - System image: **Android 9.0 Pie x86**
   - Orientation: **Portrait**

### 3. Install the Monefy Lite APK on the emulator

```bash
# Start the emulator first, then:
cd <android-sdk>/platform-tools
adb install path/to/com.monefy.app.lite.apk
```

### 4. Start the Appium server

```bash
appium --address 0.0.0.0 --port 4723
```

### 5. Desired Capabilities (`DesiredCapabilities.json`)

```json
{
  "deviceName":   "emulator-5554",
  "platformName": "android",
  "appPackage":   "com.monefy.app.lite",
  "appActivity":  "com.monefy.activities.main.MainActivity_",
  "noReset":      true
}
```

`noReset: true` preserves app state between test runs. Set it to `false` to start each run from a clean state.

---

## Running the Tests

1. Start the Android emulator.
2. Start the Appium server (`appium --address 0.0.0.0 --port 4723`).
3. Open the project in Android Studio (`monefy_app_automation/`).
4. In the **Project** panel, navigate to:
   ```
   app/src/test/java/com/example/monefy_application/MonefyTest.java
   ```
5. Right-click `MonefyTest.java` → **Run 'MonefyTest'**.

Expected output: all 5 tests pass in order (Add Income → Add Bills → Add Foods → Percentage → Delete Food Expense).

---

## Project Structure

```
monefy_mobile_app/
├── README.md                          ← This file
├── markdown.pdf                       ← Task 1 exploratory testing notes
└── monefy_app_automation/             ← Android Studio project root
    ├── build.gradle                   ← Top-level Gradle config
    ├── settings.gradle
    ├── gradle.properties
    ├── gradlew / gradlew.bat
    ├── DesiredCapabilities.json       ← Appium device config reference
    ├── gradle/wrapper/
    │   └── gradle-wrapper.properties  ← Gradle 6.5
    └── app/
        ├── build.gradle               ← Module dependencies
        ├── proguard-rules.pro
        └── src/
            ├── libs/                  ← Local JARs
            │   ├── java-client-7.4.1-all.jar
            │   ├── testng-7.0.0.jar
            │   └── log4j-*.jar
            ├── main/
            │   └── AndroidManifest.xml
            └── test/java/com/example/monefy_application/
                ├── MonefyTest.java
                ├── AddIncomeAndAssert.java
                ├── AddExpenseBillsAndAssert.java
                ├── AddExpenseFoodAndAssert.java
                ├── PercentageOfFoodExpense.java
                └── DeleteFoodExpenseAndAssert.java
```

---

## Bug Fixes Applied

The following issues were identified and fixed in this repository:

| File | Issue | Fix |
|------|-------|-----|
| `PercentageOfFoodExpense.java:18` | **Corrupted XPath** — the XPath string literally contained a nested `findElementByXPath("` prefix and a missing closing `)`, making it an invalid locator that would throw a `NoSuchElementException` at runtime. | Stripped the erroneous prefix; left a valid bare XPath string. |
| `PercentageOfFoodExpense.java:32-36` | **Broken percentage math** — integer division of `food / total` always yields `0`, and `"0".substring(2)` throws `StringIndexOutOfBoundsException`. | Replaced with `(int) Math.round((food / total) * 100)` using `double` arithmetic, producing a correct `"80%"` style string. |
| `app/build.gradle:53` | **Duplicate Appium dependency** — both the local `java-client-7.4.1-all.jar` and the Maven artifact `io.appium:java-client:4.1.2` were on the classpath, causing version-conflict errors at compile time. | Removed the stale Maven artifact; the local 7.4.1 jar is the intended dependency. |
| `build.gradle` | **Deprecated `jcenter()`** — JCenter was sunset in May 2021 and the CDN was shut down in 2022, causing `Could not resolve` build failures for all Maven dependencies. | Replaced both `jcenter()` declarations with `mavenCentral()`. |
| `MonefyTest.java` | **Undefined test execution order** — TestNG does not guarantee method execution order by default; tests that depend on accumulated app state (income → expense → delete) could run out of sequence and fail. | Added `priority = 1..5` to each `@Test` annotation to enforce the required order. |

---

## Known Limitations

- **Hardcoded balance assertions** — expected balance values (`$150.00`, `$100.00`, `$20.00`) are hardcoded. If the emulator retains data from a previous run with `noReset: true` the assertions will fail. Reset the app data or set `noReset: false` before a fresh run.
- **Absolute XPath selectors** — several locators use full hierarchy XPaths which are brittle across app version updates. Consider migrating to `By.id` or accessibility-ID locators where possible.
- **Single device / single platform** — the suite targets Android Pie on a Pixel 4 emulator only. iOS support would require separate desired capabilities and driver configuration.
