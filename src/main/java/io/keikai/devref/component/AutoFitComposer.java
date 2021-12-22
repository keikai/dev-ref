package io.keikai.devref.component;

import io.keikai.api.*;
import io.keikai.ui.Spreadsheet;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.*;

public class AutoFitComposer extends SelectorComposer {
    @Wire
    private Spreadsheet spreadsheet;
    @Wire
    private Intbox startCol;
    @Wire
    private Intbox endCol;
    @Wire
    private Checkbox useSelectionBox;

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

    @Listen("onClick = #autofitHeight")
    public void autoFitRowHeight() {
        if (useSelectionBox.isChecked()) {
            Range selectedRange = Ranges.range(spreadsheet.getSelectedSheet(), spreadsheet.getSelection());
            selectedRange.zOrderStream().forEach(range -> {
                if (range.hasMergedCell()) {
                    //only auto-fit the first row of a merged cell
                    autoFitMergedCell(range);
                } else {
                    autoFitOneCell(range);
                }
            });
        }
    }

    private void autoFitOneCell(Range range) {
        int currentHeight = range.getSheet().getRowHeight(range.getRow());
        int autofitHeight = CellOperationUtil.getAutoFitHeight(spreadsheet.getSelectedSheet(),
                range.getRow(), range.getColumn());
        if (autofitHeight != currentHeight) {
            range.setRowHeight(autofitHeight);
        }
    }

    /**
     * only change the first top cell of a vertical-merged cell
     * @param mergedCell
     */
    private void autoFitMergedCell(Range mergedCell) {
        //sum all rows' height except the 1st row
        int heightSum = 0;
        for (int r = mergedCell.getMergedRegion().getRow() + 1; r <= mergedCell.getMergedRegion().getLastRow(); r++) {
            heightSum += mergedCell.getSheet().getRowHeight(r);
        }
        int autofitHeight = CellOperationUtil.getAutoFitHeight(spreadsheet.getSelectedSheet(),
                mergedCell.getRow(), mergedCell.getColumn());
        mergedCell.toCellRange(0, 0).setRowHeight(autofitHeight - heightSum);
    }
}
