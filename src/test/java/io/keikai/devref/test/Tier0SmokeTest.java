package io.keikai.devref.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tier 0 smoke: load every page of dev-ref, assert no console error / ZK error
 * box / server-side failure, and record a structured JSON result per page for
 * cross-version comparison (Phase 2/3 of the 6.3 -> 7.0 migration plan).
 *
 * <p>The page list is scanned from the file system at runtime — no hand-written
 * page cases. Pages that cannot run under the embedded Jetty (JSP/JSF need a
 * Tomcat-like container; the project's own jetty-maven-plugin config states
 * "no JSP, JSF supported") or that need preconditions (login) are skipped with
 * a reason, which is also recorded in the JSON output.
 */
public class Tier0SmokeTest extends DevRefTestBase {
    private static final Path WEBAPP = Paths.get("src/main/webapp");

    /** page -> skip reason */
    private static final Map<String, String> SKIP = new LinkedHashMap<>();
    static {
        SKIP.put("/useCase/userPermission/main.zul",
                "requires login session (userPermission use case); covered via login flow in Phase 4");
        SKIP.put("/WEB-INF/dialog/dataValidation.zul", "WEB-INF internal dialog, not directly accessible");
        SKIP.put("/useCase/help/moreInfo.zul",
                "include-fragment of hotelVoting.zul (needs name/rating/... args), not a standalone page");
    }

    /**
     * Pages known broken (or noisy) on the 6.3 baseline itself — pre-existing
     * dev-ref issues, not migration regressions. They are still loaded and
     * their JSON records kept for cross-version compare, but they don't fail
     * the build. Verified causes noted per page.
     */
    private static final Map<String, String> KNOWN_BASELINE_ISSUES = new LinkedHashMap<>();
    static {
        String embed = "embed demo intentionally targets http://localhost:8080; console errors expected unless the app runs there";
        KNOWN_BASELINE_ISSUES.put("/advanced/embed/embed.html", embed);
        KNOWN_BASELINE_ISSUES.put("/advanced/embed/keikai.html", embed);
        KNOWN_BASELINE_ISSUES.put("/useCase/stock-search.html", embed);
    }

    /** pages needing a query string to be meaningful when opened directly */
    private static final Map<String, String> PARAMS = new LinkedHashMap<>();
    static {
        PARAMS.put("/misc/iframeeditor.zul", "?target=/WEB-INF/books/demo_sample.xlsx");
    }

    public static Collection<String> pages() throws IOException {
        try (Stream<Path> walk = Files.walk(WEBAPP)) {
            return walk.filter(Files::isRegularFile)
                    .map(p -> "/" + WEBAPP.relativize(p).toString().replace('\\', '/'))
                    .filter(p -> p.endsWith(".zul") || p.endsWith(".html")
                            || p.endsWith(".jsp") || p.endsWith(".xhtml"))
                    .filter(p -> !p.startsWith("/WEB-INF") || SKIP.containsKey(p))
                    .sorted() // fixed order: pages share WEB-INF/books
                    .collect(Collectors.toList());
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("pages")
    public void pageLoads(String page) throws Exception {
        String skipReason = skipReason(page);
        if (skipReason != null) {
            PageResult r = new PageResult();
            r.page = page;
            r.status = "skipped: " + skipReason;
            writeRecord(pageKey(page), r);
            assumeTrue(false, "SKIP " + page + ": " + skipReason);
        }

        PageResult r = smokePage(page + PARAMS.getOrDefault(page, ""));
        String known = KNOWN_BASELINE_ISSUES.get(page);
        if (known != null) {
            // still recorded for cross-version compare, but not a build failure
            assumeTrue(false, "KNOWN " + page + ": " + known);
        }
        assertEquals("loaded", r.status, "page failed to load: " + r.error);
        assertFalse(r.zkError, "ZK error box shown");
        assertTrue(r.consoleErrors.isEmpty(), "console errors: " + r.consoleErrors);
        // note: spreadsheetRendered is recorded in the JSON but not asserted —
        // some pages (e.g. collaborationEdit.zul) legitimately start without a
        // book; render regressions are caught by the cross-version JSON diff.
    }

    private static String skipReason(String page) {
        if (SKIP.containsKey(page)) {
            return SKIP.get(page);
        }
        if (page.endsWith(".jsp") || page.endsWith(".xhtml")) {
            return "JSP/JSF requires a Tomcat-like container; project's jetty config states 'no JSP, JSF supported'";
        }
        return null;
    }

    private static String pageKey(String location) {
        return location.replaceAll("^/", "").replaceAll("[/?&= ]", "_");
    }

    /** Merges all per-page records into one baseline JSON for cross-version compare. */
    @AfterAll
    public static void mergeBaseline() throws IOException {
        if (!Files.isDirectory(OUT_DIR)) {
            return;
        }
        List<String> records = new ArrayList<>();
        try (Stream<Path> files = Files.list(OUT_DIR)) {
            files.filter(p -> p.toString().endsWith(".json")
                            && !p.getFileName().toString().startsWith("baseline-"))
                    .sorted()
                    .forEach(p -> {
                        try {
                            records.add(new String(Files.readAllBytes(p), StandardCharsets.UTF_8).trim());
                        } catch (IOException ignored) {
                        }
                    });
        }
        String out = "[\n" + String.join(",\n", records) + "\n]\n";
        Files.write(OUT_DIR.resolve("baseline-" + KEIKAI_VERSION + ".json"),
                out.getBytes(StandardCharsets.UTF_8));
    }
}
