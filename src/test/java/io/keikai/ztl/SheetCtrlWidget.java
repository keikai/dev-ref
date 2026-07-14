/* SheetCtrl.java

	Purpose:
		
	Description:
		
	History:
		Mon, Jul 21, 2014 11:00:22 AM, Created by RaymondChao

Copyright (C) 2014 Potix Corporation. All Rights Reserved.

*/
package io.keikai.ztl;

import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;


/**
 * 
 * @author RaymondChao
 */
public class SheetCtrlWidget extends Widget {
	public SheetCtrlWidget(SpreadsheetWidget spreadsheet) {
		super("#" + spreadsheet.uuid());
		_out.append(".sheetCtrl");
	}
	
	/**
	 * 
	 * @param row start from 0
	 * @param column row start from 0
	 * @return
	 */
	StringBuffer out() {
		return _out;
	}

	public CellWidget getCell(int row, int column) {
		return new CellWidget(this, row, column);
	}
	
	public CellWidget getCell(String cellRef) {
		return new CellWidget(this, cellRef);
	}

	public EditorWidget getInlineEditor() {
		return new EditorWidget(this);
	}
	public EditorWidget getFormulabarEditor() {
		return new EditorWidget(this, EditorWidget.EditorType.FORMULABAR);
	}
	
	public Widget getContextMenu() {
		return this.getChild("styleMenupopup");
	}

	public void scrollLeft(int scrollLeft) {
		this.eval("sp.$scroll().scrollLeft =" + scrollLeft);
	}
	public void scrollTop(int scrollTop) {
		this.eval("sp.$scroll().scrollTop =" + scrollTop);
	}

	/**
	 * Pixel width of the left-side row-header gutter. Used to translate
	 * column-relative pixel positions (which {@code custColWidth.getStartPixel}
	 * returns) into absolute sheet-view pixel coordinates.
	 */
	public int getLeftHeadWidth() {
		return Integer.parseInt(this.eval("_wgt.getLeftheadWidth()"));
	}

	/**
	 * Pixel width of column {@code col} (0-based). Reads from the JS-side
	 * {@code custColWidth.getSize(col)} which honours custom-width entries
	 * and the sheet's default column width. Restored from commit 348010076
	 * (lost in revert e9d0fade9).
	 */
	public int getColumnWidth(int col) {
		return (int) Double.parseDouble(this.eval("custColWidth.getSize(" + col + ")"));
	}

	/**
	 * Absolute {@code offsetLeft} in pixels of column {@code col} (0-based)
	 * within the sheet view, including the left-head gutter offset.
	 * Stable replacement for the brittle {@code .zstopcell .zsw{N}} CSS-class
	 * lookup, which only matched custom-width columns and broke when a
	 * sheet had a mix of default-width and custom-width columns
	 * (KEIKAI_0700Test#testGroupRowsColumns on keikai-0596.xlsx).
	 * Restored from commit 348010076 (lost in revert e9d0fade9).
	 */
	public int getColumnOffsetLeft(int col) {
		return Integer.parseInt(this.eval("custColWidth.getStartPixel(" + col + ")"))
				+ getLeftHeadWidth();
	}
}
