package io.keikai.devref.usecase;

import io.keikai.api.Range;
import io.keikai.devref.util.RangeHelper;
import io.keikai.ui.event.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zkmax.zul.Drawer;

public class HelpTextComposer extends SelectorComposer {

    @Wire
    private Drawer helpDrawer;

    @Listen(Events.ON_CELL_CLICK + " = spreadsheet")
    public void showHelpText(CellMouseEvent e){
        Range cell = RangeHelper.getTargetRange(e);
        if (cell.getCellValue() != null && cell.getCellValue().toString().equals("Help")) {
            helpDrawer.open();
        }
    }
}
