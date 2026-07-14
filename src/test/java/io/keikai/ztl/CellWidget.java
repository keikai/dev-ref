/* CellWidget.java

	Purpose:
		
	Description:
		
	History:
		Mon, Jul 21, 2014 11:37:03 AM, Created by RaymondChao

Copyright (C) 2014 Potix Corporation. All Rights Reserved.

*/
package io.keikai.ztl;

import io.keikai.util.Converter;
import io.keikai.util.Ref;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

/**
 * 
 * @author RaymondChao
 */
public class CellWidget extends Widget {
	private static final String CELL = ".getCell(%1$d,%2$d)";

	public CellWidget(SheetCtrlWidget spreadsheet, int row, int column) {
		super(spreadsheet.out());
		_out.append(String.format(CELL, row, column));
	}
	
	public CellWidget(SheetCtrlWidget spreadsheet, String cellRef) {
		super(spreadsheet.out());
		if (isEmpty(cellRef)) {
			throw new NullPointerException("cell reference cannot be null.");
		}
		Ref ref = Converter.a1ToRef(cellRef);
		_out.append(String.format(CELL, ref.getTop(), ref.getLeft()));
	}

	public String getText() {
		return new JQuery(this).text();
	}
	
	public boolean isMerged() {
		return get("merid") != null;
	}
	
	private int toColumn(String refference) {
		char[] array = refference.toCharArray();
		double result = 0;
		for (int i = array.length - 1; i >= 0; --i) {
			result = ((array[i] - 64) * Math.pow(26, i) + result); 
		}
		return (int) result;
	}
}
