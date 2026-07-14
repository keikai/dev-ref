package io.keikai.devref.test;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Phase 0 verification: open index.zul through the embedded Jetty with a real
 * Chrome, confirm the page loads without console errors and produce the
 * structured JSON record + screenshot used for cross-version compare.
 */
public class IndexSmokeTest extends DevRefTestBase {

    @Test
    public void indexPageLoads() throws Exception {
        PageResult r = smokePage("/index.zul");
        assertEquals("loaded", r.status);
        assertFalse(r.zkError, "ZK error box shown");
        List<String> errors = r.consoleErrors;
        assertTrue(errors.isEmpty(), "console errors: " + errors);
    }

    @Test
    public void spreadsheetPageRenders() throws Exception {
        PageResult r = smokePage("/importAnExcelFile/import-src.zul");
        assertEquals("loaded", r.status);
        assertFalse(r.zkError, "ZK error box shown");
        assertTrue(r.hasSpreadsheet, "spreadsheet widget not detected");
        assertTrue(r.spreadsheetRendered, "spreadsheet grid empty");
        assertTrue(r.consoleErrors.isEmpty(), "console errors: " + r.consoleErrors);
    }
}
