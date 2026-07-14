/* Editor.java

	Purpose:
		
	Description:
		
	History:
		Mon, Jul 21, 2014  2:16:39 PM, Created by RaymondChao

Copyright (C) 2014 Potix Corporation. All Rights Reserved.

*/
package io.keikai.ztl;

import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

/**
 * 
 * @author RaymondChao
 */
public class EditorWidget extends Widget {
	
	public enum EditorType {
	    INLINE,
	    FORMULABAR
	}
	
	private static final String EDITOR = ".dp.getEditor(%1$s)";
	
	public EditorWidget(SheetCtrlWidget spreadsheet) {
		super(spreadsheet.out());
		_out.append(String.format(EDITOR, ""));
	}
	
	public EditorWidget(SheetCtrlWidget spreadsheet, EditorType type) {
		super(spreadsheet.out());
		_out.append(String.format(EDITOR, type == EditorType.FORMULABAR ? "'formulabarEditing'" : ""));
	}

	public String getValue() {
		return (String) get("value");
	}
}
