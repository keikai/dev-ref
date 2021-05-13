/* BookSheetComposer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Nov 18, 2010 5:42:27 PM , Created by Sam
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package io.keikai.devref.model;

import io.keikai.api.*;
import io.keikai.api.SheetProtection;
import io.keikai.api.model.*;
import io.keikai.ui.Spreadsheet;
import io.keikai.ui.event.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.Label;

/**
 * Demonstrate how to change different sheet
 * @author Hawk
 *
 */
@SuppressWarnings("serial")
public class ProtectionComposer extends SelectorComposer<Component>{
	
	@Wire
	private Spreadsheet ss;
	@Wire
	private Label status;
	@Wire
	private Label lockStatus;
	private static final String PASSWORD = "mypass";
	static private SheetProtection PROTECTION_WITH_SELECTION = SheetProtection.Builder.create().withPassword(PASSWORD).withSelectLockedCellsAllowed(true).withSelectUnlockedCellsAllowed(true).build();

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		updateSheetProtectionStatus(ss.getSelectedSheet());
	}
	
	@Listen("onClick = #toggleProtection")
	public void toggleProtection(){
		Sheet selectedSheet = ss.getSelectedSheet();
		if (selectedSheet.isProtected()){
			Ranges.range(selectedSheet).unprotectSheet(PASSWORD);
		}else{
			Ranges.range(selectedSheet).protectSheet(PROTECTION_WITH_SELECTION);
		}
		updateSheetProtectionStatus(selectedSheet);
	}
	
	@Listen("onSheetSelect = #ss")
	public void selectSheet(SheetSelectEvent event) {
		updateSheetProtectionStatus(event.getSheet());
	}
	
	private void updateSheetProtectionStatus(Sheet sheet){
		status.setValue(Boolean.toString(sheet.isProtected()));
	}
	
	@Listen("onClick = #toggleLock")
	public void toggleLock(){
		Range selection = Ranges.range(ss.getSelectedSheet(), ss.getSelection());

		CellStyle oldStyle = selection.getCellStyle();
		CellStyle newStyle = selection.getCellStyleHelper().builder(oldStyle)
				.locked(!oldStyle.isLocked()).build();
		selection.setCellStyle(newStyle);
		updateCellLockedStatus(newStyle.isLocked());
	}
	
	@Listen("onCellSelection = #ss")
	public void selectCells(CellSelectionEvent event) {
		CellStyle style = Ranges.range(ss.getSelectedSheet(), ss.getSelection()).getCellStyle();
		updateCellLockedStatus(style.isLocked());
	}
	
	private void updateCellLockedStatus(Boolean status){
		lockStatus.setValue(status.toString());
	}
}