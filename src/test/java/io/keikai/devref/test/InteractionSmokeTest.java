package io.keikai.devref.test;

import io.keikai.ztl.CellWidget;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Phase 4 basic interaction sweep over the API-heavy page categories
 * (manipulatingBookModel / advanced / useCase): on every page with a rendered
 * spreadsheet, click a cell, type a value, hit Enter, then re-check browser
 * console and ZK error box. The cell text after edit is recorded in the JSON
 * record (interactionCellText) for cross-version compare rather than asserted
 * — pages with protected sheets legitimately refuse the edit.
 */
public class InteractionSmokeTest extends DevRefTestBase {
    private static final Path WEBAPP = Paths.get("src/main/webapp");
    private static final List<String> CATEGORIES =
            Arrays.asList("manipulatingBookModel", "advanced", "useCase");

    /**
     * Pages excluded from the scan: not valid standalone interaction targets.
     * /useCase/userPermission/main.zul needs a login session and is covered by
     * {@link UserPermissionFlowTest} instead.
     */
    private static final Set<String> EXCLUDE =
            Collections.singleton("/useCase/userPermission/main.zul");

    /**
     * Pages whose composer shows a dialog on edit by design (validation alerts,
     * protected-sheet warnings). The dialog text is recorded instead of failing.
     */
    private static final Map<String, String> EXPECTED_DIALOG = new LinkedHashMap<>();
    static {
        EXPECTED_DIALOG.put("/useCase/vendorClient.zul",
                "edit triggers Clients.alert(\"Please enter a company name\") validation by design");
    }

    public static Collection<String> pages() throws IOException {
        List<String> result = new ArrayList<>();
        for (String category : CATEGORIES) {
            try (Stream<Path> walk = Files.walk(WEBAPP.resolve(category))) {
                result.addAll(walk.filter(Files::isRegularFile)
                        .map(p -> "/" + WEBAPP.relativize(p).toString().replace('\\', '/'))
                        .filter(p -> p.endsWith(".zul"))
                        .filter(p -> !EXCLUDE.contains(p))
                        .sorted()
                        .collect(Collectors.toList()));
            }
        }
        return result;
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("pages")
    public void basicEditInteraction(String page) throws Exception {
        connect(page.replace(" ", "%20"));
        if ("/advanced/collaborationEdit.zul".equals(page)) {
            // page starts without a book: pick the first one from the list
            click(jq(".z-listbox .z-listitem").first());
            waitResponse(true);
        }
        boolean rendered = Boolean.parseBoolean(
                getEval("!!jq('.zssheet')[0] && jq('.zscell').length > 0"));
        assumeTrue(rendered, "no rendered spreadsheet on this page");

        CellWidget cell = range("B2");
        try {
            click(cell);
            waitResponse(true);
            typeInCell(cell, "kk7test");
            waitResponse(true);
        } catch (org.openqa.selenium.ElementNotInteractableException e) {
            // cell is protected or covered by page chrome — a legitimate page
            // design (e.g. protectSheet(VIEW_ONLY)); record and skip
            PageResult blocked = new PageResult();
            blocked.page = page + "#interaction";
            blocked.status = "edit-blocked: " + e.getClass().getSimpleName();
            writeRecord(pageKey(page) + "_interaction", blocked);
            assumeTrue(false, "B2 not editable on " + page + " (" + e.getClass().getSimpleName() + ")");
        }

        PageResult r = new PageResult();
        r.page = page + "#interaction";
        r.status = "interacted";
        r.hasSpreadsheet = true;
        r.spreadsheetRendered = true;
        r.zkError = hasError();
        r.consoleErrors = getConsoleErrors();
        r.error = "cellText=" + safeCellText();
        r.screenshot = screenshot(pageKey(page) + "_interaction").toString();
        writeRecord(pageKey(page) + "_interaction", r);

        if (r.zkError && EXPECTED_DIALOG.containsKey(page)) {
            r.status = "interacted-with-expected-dialog";
            writeRecord(pageKey(page) + "_interaction", r);
        } else {
            assertFalse(r.zkError, "ZK error box after interaction");
        }
        assertTrue(r.consoleErrors.isEmpty(),
                "console errors after interaction: " + r.consoleErrors);
    }

    private static String pageKey(String location) {
        return location.replaceAll("^/", "").replaceAll("[/?&= ]", "_");
    }

    private String safeCellText() {
        try {
            return range("B2").getText();
        } catch (Exception e) {
            return "<unreadable: " + e.getClass().getSimpleName() + ">";
        }
    }
}
