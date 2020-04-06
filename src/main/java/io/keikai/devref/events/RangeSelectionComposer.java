package io.keikai.devref.events;

import io.keikai.api.*;
import io.keikai.ui.Spreadsheet;
import io.keikai.ui.event.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.*;

/**
 * A use case of CellSelectionEvent.
 * @author Hawk
 *
 */
public class RangeSelectionComposer extends SelectorComposer<Component>{

	private static final long serialVersionUID = 1L;
	
	@Wire
	private Spreadsheet ss;
	@Wire
	private Window dialog;
	
	private CellSelectionListener cellSelectionListener = new CellSelectionListener();
	private NoEditListener noEditListener = new NoEditListener();
	
	/**
	 * When opening the dialog to select a range, we can hide edit UI and cancel onStartEditing event to forbid users editing.
	 */
	@Listen("onClick = #open")
	public void open(){
		dialog.setVisible(true);
		ss.addEventListener("onCellSelection", cellSelectionListener);
		ss.focus();
		Textbox rangeBox  = (Textbox)dialog.getFellow("rangeBox");
		rangeBox.setValue("");
		//forbid users doing anything
		ss.setShowFormulabar(false);
		ss.setShowToolbar(false);
		ss.setShowContextMenu(false);
		ss.setShowSheetbar(false);
		ss.addEventListener("onStartEditing", noEditListener);
	}
	
	@Listen(io.keikai.ui.event.Events.ON_CELL_SELECTION + "= #dialog")
	public void onCellSelection(CellSelectionEvent event){
		Textbox rangeBox  = (Textbox)dialog.getFellow("rangeBox");
		Range selection = Ranges.range(event.getSheet(), event.getArea());
		if (selection.isWholeRow()){
			rangeBox.setValue(Ranges.getRowRefString(event.getRow()));
		}else if (selection.isWholeColumn()){
			rangeBox.setValue(Ranges.getColumnRefString(event.getColumn()));
		}else{
			rangeBox.setValue(Ranges.getAreaRefString(event.getSheet(), event.getArea()));
		}
	}
	
	
	@Listen("onClick = #dialog #ok")
	public void hideDialog(Event event){
		//could validate user selection
		dialog.setVisible(false);
		event.stopPropagation();
		ss.removeEventListener("onCellSelection", cellSelectionListener); //reduce traffic to a server
		//recover editing actions back
		ss.setShowFormulabar(true);
		ss.setShowToolbar(true);
		ss.setShowContextMenu(true);
		ss.setShowSheetbar(true);
		ss.removeEventListener("onStartEditing", noEditListener); //back to normal
	}
	
	class CellSelectionListener implements EventListener<CellSelectionEvent>{
		@Override
		public void onEvent(CellSelectionEvent event) throws Exception {
			Events.postEvent(dialog, event);
		}
	}
	
	/**
	 * Prevent users from editing cells.
	 * @author hawk
	 *
	 */
	class NoEditListener implements EventListener<StartEditingEvent>{

		@Override
		public void onEvent(StartEditingEvent event) throws Exception {
			event.cancel();			
		}
	}
}




