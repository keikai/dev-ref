package io.keikai.devref.advanced;

import io.keikai.api.*;
import io.keikai.devref.util.RangeHelper;
import io.keikai.ui.event.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.*;

/**
 * @author Hawk
 */
public class FormControlComposer extends SelectorComposer<Component> {

	private static final long serialVersionUID = 1L;
	private static final String CELL = "CELL";
	@Wire
	private Popup popup;
	@Wire
	private Listbox optionBox;
	

	@Listen(Events.ON_CELL_CLICK + "= #ss")
	public void showOptions(CellMouseEvent e){
		Range clickedCell = RangeHelper.getTargetRange(e);
		if (e.getColumn() == 2
			&& clickedCell.getCellData().isBlank()){
			popup.open(e.getClientx(),e.getClienty());
			optionBox.setAttribute(CELL, clickedCell);
		}
	}

	@Listen(org.zkoss.zk.ui.event.Events.ON_SELECT  + "=#optionBox")
	public void fillOption(){
		Range clickedCEll = (Range)optionBox.getAttribute(CELL);
		clickedCEll.setCellValue(optionBox.getSelectedItem().getValue());
		optionBox.clearSelection();
	}
}



