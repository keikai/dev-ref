package io.keikai.devref.advanced;

import io.keikai.ui.Spreadsheet;
import org.zkoss.zk.au.out.AuInvoke;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Popup;

/**
 * a custom popup that opens aside a cell.
 *
 */
public class SpreadsheetCellPopup extends Popup {

	/**
	 * open this popup around a Spreadsheet cell.
	 * @see Popup#open(Component, String)
	 * @param ref Spreadsheet
	 * @param row cell row index
	 * @param col cell column index
	 * @param position popup position.
	 */
	public void openAtCell(Spreadsheet ref, int row, int col, String position) {
		response("popup", new AuInvoke(this, "openAtCell", new Object[] { ref.getUuid(), null, position, null, row, col }));
		disableClientUpdate(true);
		try {
			super.setVisibleDirectly(true);
		} finally {
			disableClientUpdate(false);
		}
	}
	
}
