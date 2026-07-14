/* ClientSpreadsheet.java

	Purpose:
		
	Description:
		
	History:
		Fri, Jul 11, 2014  6:56:24 PM, Created by RaymondChao

Copyright (C) 2014 Potix Corporation. All Rights Reserved.

*/
package io.keikai.ztl;

import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

/**
 * @author RaymondChao
 */
public class SpreadsheetWidget extends Widget {

	static String SPREADSHEET = "@spreadsheet";

	private SheetCtrlWidget sheetCtrl;

	public SpreadsheetWidget() {
		this(new JQuery(SPREADSHEET));
	}

	public SpreadsheetWidget(JQuery jquery) {
		super(jquery);
		sheetCtrl = new SheetCtrlWidget(this);
	}

	public SheetCtrlWidget getSheetCtrl() {
		return sheetCtrl;
	}
}
