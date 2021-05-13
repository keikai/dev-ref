package io.keikai.devref;

import io.keikai.ui.Spreadsheet;
import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;

/**
 * This is used to be a system-level composer.
 * Purpose:
 * import a file with query string: <br/>
 * - file=[path] <br/>
 * <code>?file=/WEB-INF/books/demo_sample.xlsx</code>
 *
 *
 */
public class ParameterImportComposer extends SelectorComposer {
    @Wire("spreadsheet")
    private Spreadsheet spreadsheet;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        String path = Executions.getCurrent().getParameter("file");
        if (path != null && spreadsheet != null) {
            spreadsheet.setSrc(path);
        }
    }
}
