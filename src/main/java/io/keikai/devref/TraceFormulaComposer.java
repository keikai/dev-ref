package io.keikai.devref;

import io.keikai.api.*;
import io.keikai.ui.Spreadsheet;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.*;

/**
 * Demonstrate how to trace precedents/dependents of a formula.
 *
 * @author Hawk
 */
public class TraceFormulaComposer extends SelectorComposer<Component> {

    @Wire
    private Spreadsheet spreadsheet;
    @Wire
    private Label cellRef;

    @Wire("listbox")
    private Listbox relationBox;
    ListModelSet<Range> rangeList = new ListModelSet<>();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        relationBox.setModel(rangeList);
    }

    @Listen("onClick = #precedent")
    public void tracePrecedents() {
        cellRef.setValue(spreadsheet.getCellFocus().asString());
        spreadsheet.tracePrecedents(spreadsheet.getSelectedSheet(), spreadsheet.getCellFocus());
        rangeList.clear();
        rangeList.addAll(Ranges.range(spreadsheet.getSelectedSheet(), spreadsheet.getSelection()).getDirectPrecedents());
    }

    @Listen("onClick = #dependent")
    public void traceDependents() {
        cellRef.setValue(spreadsheet.getCellFocus().asString());
        spreadsheet.traceDependents(spreadsheet.getSelectedSheet(), spreadsheet.getCellFocus());
        rangeList.clear();
        rangeList.addAll(Ranges.range(spreadsheet.getSelectedSheet(), spreadsheet.getSelection()).getDirectDependents());
    }

    @Listen("onSelect = listbox")
    public void focusCell(Event event){
        Range range = rangeList.getSelection().iterator().next();
        if (!range.getSheet().getSheetName().equals(spreadsheet.getSelectedSheetName())){
            spreadsheet.setSelectedSheet(range.getSheet().getSheetName());
        }
        spreadsheet.focusTo(range.getRow(), range.getColumn());
    }

}
