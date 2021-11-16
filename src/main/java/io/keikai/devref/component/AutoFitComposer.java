package io.keikai.devref.component;

import io.keikai.api.Ranges;
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

    @Listen("onClick = #autofit")
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

    /**
     * use case: populate data from a database dynamically, invoke auto-fit from java, so that end users don't need to manually resize every column.
     */
    @Listen("onClick = #populate")
    public void populateAutoFit(){
        int maxColumn = 9;
        for (int row = 0; row <100 ; row ++){
            for (int col = 0 ; col <= maxColumn ; col++){
                Ranges.range(spreadsheet.getSelectedSheet(), row, col).setCellValue(String.format("data loaded dynamically %s-%s", row, col));
            }
        }
        spreadsheet.setAutofitColumnWidth(spreadsheet.getSelectedSheet().getInternalSheet(), 0, maxColumn);
    }
}
