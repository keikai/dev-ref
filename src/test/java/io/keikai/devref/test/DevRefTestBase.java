package io.keikai.devref.test;

import io.keikai.ztl.CellWidget;
import io.keikai.ztl.SpreadsheetWidget;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.zkoss.test.webdriver.WebDriverTestCase;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Base class for dev-ref smoke/interaction tests, runnable against both the
 * 6.3 and 7.0 checkouts (same test sources, keikai version comes from the pom).
 * Provides "open page, wait for AU idle, collect console errors, screenshot,
 * dump a structured JSON record" building blocks for cross-version compare.
 */
public abstract class DevRefTestBase extends WebDriverTestCase {

    protected SpreadsheetWidget getSpreadsheet() {
        return new SpreadsheetWidget();
    }

    protected CellWidget range(String a1notation) {
        return getSpreadsheet().getSheetCtrl().getCell(a1notation);
    }

    /**
     * Double-clicks a cell to open the inline editor, types the text and
     * commits with Enter. (Replaces the old CellWidget.type, which needed
     * the test case's Actions/driver.)
     */
    protected void typeInCell(CellWidget cell, String text) {
        getActions().doubleClick(toElement(cell)).perform();
        waitResponse(true);
        String editorId = jq(".zsedit").attr("id");
        org.openqa.selenium.WebElement editor =
                driver.findElement(By.id(editorId + "-real"));
        editor.sendKeys(text, Keys.ENTER);
        waitResponse(true);
    }

    /** Output directory for JSON records and screenshots. */
    protected static final Path OUT_DIR = Paths.get(
            System.getProperty("smoke.out", "target/smoke"));

    /** The keikai version under test, stamped into every record. */
    protected static final String KEIKAI_VERSION =
            System.getProperty("keikai.version", "unknown");

    @Override
    protected org.openqa.selenium.chrome.ChromeOptions getWebDriverOptions() {
        org.openqa.selenium.chrome.ChromeOptions options = super.getWebDriverOptions();
        org.openqa.selenium.logging.LoggingPreferences prefs =
                new org.openqa.selenium.logging.LoggingPreferences();
        prefs.enable(LogType.BROWSER, Level.SEVERE);
        options.setCapability("goog:loggingPrefs", prefs);
        return options;
    }

    /** Collects browser console entries at SEVERE level or above. */
    protected List<String> getConsoleErrors() {
        List<String> errors = new ArrayList<>();
        for (LogEntry entry : driver.manage().logs().get(LogType.BROWSER).getAll()) {
            if (entry.getLevel().intValue() >= Level.SEVERE.intValue()) {
                errors.add(entry.getMessage());
            }
        }
        return errors;
    }

    /** Saves a screenshot named after the page, returns its path. */
    protected Path screenshot(String pageKey) throws IOException {
        Files.createDirectories(OUT_DIR);
        byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Path file = OUT_DIR.resolve(pageKey + ".png");
        Files.write(file, png);
        return file;
    }

    /** Whether a rendered keikai spreadsheet grid is present and non-empty. */
    protected boolean isSpreadsheetRendered() {
        return Boolean.parseBoolean(getEval("!!jq('.zssheet')[0] && jq('.zscell').length > 0"));
    }

    /** Whether the page contains a keikai spreadsheet component at all. */
    protected boolean hasSpreadsheet() {
        return Boolean.parseBoolean(getEval("!!jq('.zssheet')[0]"));
    }

    /**
     * Opens a page, waits for AU idle, and writes a structured JSON record
     * (page, status, console errors, spreadsheet render state, screenshot path).
     */
    protected PageResult smokePage(String location) throws IOException {
        String pageKey = location.replaceAll("^/", "").replaceAll("[/?&= ]", "_");
        location = location.replace(" ", "%20");
        PageResult r = new PageResult();
        r.page = location;
        try {
            if (location.endsWith(".zul")) {
                connect(location);
                waitResponse(true);
            } else {
                // plain HTML page: no ZK Au engine, so skip the ZK-aware waits
                String url = getAddress() + location;
                int code = getStatusCode(url);
                if (code != 200) {
                    throw new AssertionError("HTTP " + code + " from " + url);
                }
                WebDriver d = getWebDriver();
                d.get(url);
                new org.openqa.selenium.support.ui.WebDriverWait(d,
                        java.time.Duration.ofMillis(getTimeout()))
                        .until(x -> "complete".equals(
                                ((org.openqa.selenium.JavascriptExecutor) x)
                                        .executeScript("return document.readyState")));
                sleep(500);
            }
            r.status = "loaded";
            if (location.endsWith(".zul")) {
                r.hasSpreadsheet = hasSpreadsheet();
                r.spreadsheetRendered = r.hasSpreadsheet && isSpreadsheetRendered();
                r.zkError = hasError();
            }
            r.consoleErrors = getConsoleErrors();
            r.screenshot = screenshot(pageKey).toString();
        } catch (AssertionError | Exception e) {
            r.status = "failed";
            r.error = String.valueOf(e);
            try {
                r.screenshot = screenshot(pageKey).toString();
            } catch (Exception ignored) {
            }
        }
        writeRecord(pageKey, r);
        return r;
    }

    protected void writeRecord(String pageKey, PageResult r) throws IOException {
        Files.createDirectories(OUT_DIR);
        Path file = OUT_DIR.resolve(pageKey + ".json");
        Files.write(file, r.toJson().getBytes(StandardCharsets.UTF_8));
    }

    /** One page's smoke result; serialized to JSON for cross-version diffing. */
    public static class PageResult {
        public String page;
        public String status;           // loaded | failed
        public boolean hasSpreadsheet;
        public boolean spreadsheetRendered;
        public boolean zkError;
        public List<String> consoleErrors = new ArrayList<>();
        public String screenshot;
        public String error;

        public String toJson() {
            StringBuilder sb = new StringBuilder("{\n");
            sb.append("  \"keikaiVersion\": ").append(q(KEIKAI_VERSION)).append(",\n");
            sb.append("  \"page\": ").append(q(page)).append(",\n");
            sb.append("  \"status\": ").append(q(status)).append(",\n");
            sb.append("  \"hasSpreadsheet\": ").append(hasSpreadsheet).append(",\n");
            sb.append("  \"spreadsheetRendered\": ").append(spreadsheetRendered).append(",\n");
            sb.append("  \"zkError\": ").append(zkError).append(",\n");
            sb.append("  \"consoleErrors\": [");
            for (int i = 0; i < consoleErrors.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(q(consoleErrors.get(i)));
            }
            sb.append("],\n");
            sb.append("  \"screenshot\": ").append(q(screenshot)).append(",\n");
            sb.append("  \"error\": ").append(q(error)).append("\n");
            sb.append("}\n");
            return sb.toString();
        }

        private static String q(String s) {
            if (s == null) return "null";
            StringBuilder sb = new StringBuilder("\"");
            for (char c : s.toCharArray()) {
                switch (c) {
                    case '"': sb.append("\\\""); break;
                    case '\\': sb.append("\\\\"); break;
                    case '\n': sb.append("\\n"); break;
                    case '\r': sb.append("\\r"); break;
                    case '\t': sb.append("\\t"); break;
                    default:
                        if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                        else sb.append(c);
                }
            }
            return sb.append('"').toString();
        }
    }
}
