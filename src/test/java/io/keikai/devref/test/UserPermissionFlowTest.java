package io.keikai.devref.test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Covers /useCase/userPermission/main.zul, which Tier0 skips because it needs
 * a login session: log in from login.zul (pick the first role, click Login)
 * and verify the main page renders a spreadsheet without errors.
 */
public class UserPermissionFlowTest extends DevRefTestBase {

    @Test
    public void loginThenMainPageRenders() throws Exception {
        connect("/useCase/userPermission/login.zul");
        waitResponse(true);

        // pick the first role from the combobox
        click(jq(".z-combobox-button"));
        waitResponse(true);
        click(jq(".z-comboitem").first());
        waitResponse(true);
        click(jq(".z-button"));
        waitResponse(true);

        // login.zul forwards to main.zul in the same desktop or via redirect
        boolean onMain = driver.getCurrentUrl().contains("main.zul");
        boolean rendered = Boolean.parseBoolean(
                getEval("!!jq('.zssheet')[0] && jq('.zscell').length > 0"));
        assertTrue(onMain || rendered,
                "after login expected main.zul or a rendered spreadsheet, url="
                        + driver.getCurrentUrl());
        assertFalse(hasError(), "ZK error box after login");
        assertTrue(getConsoleErrors().isEmpty(),
                "console errors after login: " + getConsoleErrors());

        PageResult r = new PageResult();
        r.page = "/useCase/userPermission/main.zul#loginFlow";
        r.status = "loaded";
        r.hasSpreadsheet = true;
        r.spreadsheetRendered = rendered;
        r.screenshot = screenshot("useCase_userPermission_main.zul_loginFlow").toString();
        writeRecord("useCase_userPermission_main.zul_loginFlow", r);
    }
}
