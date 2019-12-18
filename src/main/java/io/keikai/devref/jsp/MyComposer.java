package io.keikai.devref.jsp;

import io.keikai.ui.Spreadsheet;
import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;

public class MyComposer extends SelectorComposer<Component> {

    @Wire
    private Spreadsheet myzss;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        //initialize a Spreadsheet here e.g. load data from a database, set Spreadsheet setter, Range API
        String file = Executions.getCurrent().getParameter("file");
        if (file != null) {
            myzss.setSrc("/WEB-INF/books/" + file);
        }
    }

    //implement your business logic by adding event listeners
}
