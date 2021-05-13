/* BookSheetComposer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Nov 18, 2010 5:42:27 PM , Created by Sam
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package io.keikai.devref.component;

import java.util.ArrayList;
import java.util.List;

import io.keikai.ui.Spreadsheet;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

/**
 * Demonstrate how to change different sheet
 * @author Hawk
 *
 */
@SuppressWarnings("serial")
public class SelectSheetComposer extends SelectorComposer<Component>{
	
	@Wire
	Listbox sheetBox;
	@Wire
	Spreadsheet spreadsheet;
	//override
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		List<String> sheetNames = new ArrayList<String>();
		int sheetSize = spreadsheet.getBook().getNumberOfSheets();
		for (int i = 0; i < sheetSize; i++){
			sheetNames.add(spreadsheet.getBook().getSheetAt(i).getSheetName());
		}
		
		sheetBox.setModel(new ListModelList<String>(sheetNames));
	}
	
	@SuppressWarnings("rawtypes")
	@Listen("onSelect = #sheetBox")
	public void selectSheet(SelectEvent event) {
		spreadsheet.setSelectedSheet((String)event.getSelectedObjects().iterator().next());
	}
}