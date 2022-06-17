package io.keikai.devref.model;

import io.keikai.devref.util.SizeHelper;
import io.keikai.ui.Spreadsheet;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;

public class ResetDefaultComposer extends SelectorComposer<Component> {

    @Wire
    private Spreadsheet ss;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        SizeHelper.resetDefaultHeightWidth(ss.getBook());
    }

}
