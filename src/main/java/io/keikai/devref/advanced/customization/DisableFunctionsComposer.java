package io.keikai.devref.advanced.customization;

import io.keikai.ui.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;

/**
 * This class demonstrates how to disable functions.
 * @author Hawk
 *
 */
@SuppressWarnings("serial")
public class DisableFunctionsComposer extends SelectorComposer<Component> {

	@Wire
	private Spreadsheet ss;
	
	@Listen("onCheck = #add")
	public void disableAdd(CheckEvent event) {
		ss.disableUserAction(AuxAction.ADD_SHEET, !event.isChecked());
	}
	
	@Listen("onCheck = #del")
	public void disableDel(CheckEvent event) {
		ss.disableUserAction(AuxAction.DELETE_SHEET, !event.isChecked());
	}
	
	@Listen("onCheck = #copy")
	public void disableCopy(CheckEvent event) {
		ss.disableUserAction(AuxAction.COPY_SHEET, !event.isChecked());
	}
	
	@Listen("onCheck = #hide")
	public void disableHide(CheckEvent event) {
		ss.disableUserAction(AuxAction.HIDE_SHEET, !event.isChecked());
		ss.disableUserAction(AuxAction.UNHIDE_SHEET, !event.isChecked());
	}
	
	@Listen("onCheck = #rename")
	public void disableRename(CheckEvent event) {
		ss.disableUserAction(AuxAction.RENAME_SHEET, !event.isChecked());
	}
	
	@Listen("onCheck = #move")
	public void disableMove(CheckEvent event) {
		ss.disableUserAction(AuxAction.MOVE_SHEET_LEFT, !event.isChecked());
		ss.disableUserAction(AuxAction.MOVE_SHEET_RIGHT, !event.isChecked());
	}
	
	@Listen("onCheck = #protect")
	public void disableProtect(CheckEvent event) {
		ss.disableUserAction(AuxAction.PROTECT_SHEET, !event.isChecked());
	}

	@Listen("onCheck = #chart")
	public void disableChart(CheckEvent event) {
		ss.disableUserAction(AuxAction.INSERT_CHART, !event.isChecked());
		ss.disableUserAction(AuxAction.INSERT_PICTURE, !event.isChecked());
	}
}