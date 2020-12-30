package io.keikai.devref;

import io.keikai.ui.Spreadsheet;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.Label;

/**
 * Demonstrate how to trace precedents/dependents of a formula.
 * @author Hawk
 */
public class TraceFormulaComposer extends SelectorComposer<Component> {

	@Wire
	private Spreadsheet spreadsheet;
	@Wire
	private Label cellRef;

	@Listen("onClick = #precedent")
	public void tracePrecedents() {
		cellRef.setValue(spreadsheet.getCellFocus().asString());
		spreadsheet.tracePrecedents(spreadsheet.getSelectedSheet(), spreadsheet.getCellFocus());
	}

	@Listen("onClick = #dependent")
	public void traceDependents() {
		cellRef.setValue(spreadsheet.getCellFocus().asString());
		spreadsheet.traceDependents(spreadsheet.getSelectedSheet(), spreadsheet.getCellFocus());
	}

}
