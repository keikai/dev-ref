package io.keikai.devref;

import io.keikai.ui.Spreadsheet;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.Intbox;

public class AutoFitComposer extends SelectorComposer {
    @Wire
    private Spreadsheet spreadsheet;
    @Wire
    private Intbox startCol;
    @Wire
    private Intbox endCol;

    @Listen("onClick = button")
    public void autoFit(){
        int start = startCol.getValue();
        int end = endCol.getValue();
        if (end < start){
            return;
        }
        if (start == end) {
            spreadsheet.setAutofitColumnWidth(spreadsheet.getSelectedSheet().getInternalSheet(), start);
        }else{
            spreadsheet.setAutofitColumnWidth(spreadsheet.getSelectedSheet().getInternalSheet(), start, end);
        }
    }
}
