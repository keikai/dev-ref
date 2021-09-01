package io.keikai.devref.advanced;

import io.keikaiex.formula.ZKUDFFinder;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.util.WebAppInit;

/**
 * register a {@link org.zkoss.poi.ss.formula.functions.FreeRefFunction}.
 */
public class FunctionRegistration implements WebAppInit {
    @Override
    public void init(WebApp wapp) throws Exception {
        ZKUDFFinder.putFunction("mycell", new MyCell());
//        ZKUDFFinder.putFunction("cell", new MyCell()); //limitation: doesn't work, cell is registered internally
    }
}
