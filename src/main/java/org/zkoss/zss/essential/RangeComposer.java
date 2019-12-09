package org.zkoss.zss.essential;

import io.keikai.api.Ranges;
import io.keikai.ui.Spreadsheet;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;

/**
 * Demonstrate API usage for Ranges
 * 
 * @author Hawk
 * 
 */
public class RangeComposer extends SelectorComposer<Component> {

	@Wire
	private Spreadsheet spreadsheet;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		// a book
		Ranges.range(spreadsheet.getBook());
		// a sheet
		Ranges.range(spreadsheet.getSelectedSheet());
		// a row
		Ranges.range(spreadsheet.getSelectedSheet(), "A1").toRowRange();
		// multiple cells
		Ranges.range(spreadsheet.getSelectedSheet(), "A1:B4");
		Ranges.range(spreadsheet.getSelectedSheet(), 0, 0, 3, 1);
		// a cell
		Ranges.range(spreadsheet.getSelectedSheet(),  3, 3);
	}
}
