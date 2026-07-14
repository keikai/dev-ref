package io.keikai.devref.test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.JavascriptExecutor;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Phase 4: dump the spreadsheet's DOM structure (tag + CSS class inventory
 * with counts) for representative pages, one JSON per page, to diff 6.3 vs
 * 7.0. CSS class renames are breaking changes for customers with custom
 * styling that functional tests cannot catch.
 */
public class DomDumpTest extends DevRefTestBase {

    public static Collection<String> pages() {
        return Arrays.asList(
                "/importAnExcelFile/import-src.zul",   // plain grid
                "/manipulatingBookModel/cellStyle.zul", // styles, toolbar
                "/useCase/budget.zul");                 // real-world sheet
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("pages")
    public void dumpSpreadsheetDom(String page) throws Exception {
        connect(page);
        String json = (String) ((JavascriptExecutor) driver).executeScript(
                "var root = document.querySelector('.zssheet');" +
                "if (!root) root = document.body;" +
                "var tags = {}, classes = {};" +
                "var all = [root].concat(Array.prototype.slice.call(root.querySelectorAll('*')));" +
                "all.forEach(function (el) {" +
                "  var t = el.tagName.toLowerCase();" +
                "  tags[t] = (tags[t] || 0) + 1;" +
                "  Array.prototype.forEach.call(el.classList, function (c) {" +
                "    if (/^z-|^zs/.test(c)) { classes[c] = (classes[c] || 0) + 1; }" +
                "  });" +
                "});" +
                "var sortObj = function (o) {" +
                "  var r = {};" +
                "  Object.keys(o).sort().forEach(function (k) { r[k] = o[k]; });" +
                "  return r;" +
                "};" +
                "return JSON.stringify({tags: sortObj(tags), classes: sortObj(classes)}, null, 2);");
        assertNotNull(json);

        Path outDir = Paths.get(System.getProperty("domdump.out", "target/domdump"));
        Files.createDirectories(outDir);
        String key = page.replaceAll("^/", "").replaceAll("[/?&= ]", "_");
        Files.write(outDir.resolve(key + ".json"), json.getBytes(StandardCharsets.UTF_8));
    }
}
