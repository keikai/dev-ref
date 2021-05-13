package io.keikai.devref.util;

import org.zkoss.poi.ss.usermodel.ZssContext;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.util.*;

import java.util.*;

/**
 * set locale for spreadsheet for each execution because it stores ZssContext in a ThreadLocal which is independent of each request.
 */
public class LocaleExecutionInit implements ExecutionInit {
    @Override
    public void init(Execution exec, Execution parent) throws Exception {
        ZssContext.setThreadLocal(new ZssContext(Locale.GERMAN, -1));
    }
}
