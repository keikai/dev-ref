package org.zkoss.zss.essential.advanced.customization;

import java.util.HashMap;

import io.keikai.ui.*;
import io.keikai.ui.event.*;
import io.keikai.ui.event.Events;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

/**
 * @author Hawk
 *
 */
@SuppressWarnings("serial")
public class MyContextMenuComposer extends SelectorComposer<Component> {

	@Wire
	private Menupopup myContext;
	@Wire
	private Window dialog;
	@Wire("#dialog #content")
	private Label content;

	@Listen("onClick = #display")
	public void display(MouseEvent event) {
		CellMouseEvent cellMouseEvent = (CellMouseEvent)myContext.getAttribute("event");
		String message = "Selection: " + ((Spreadsheet)cellMouseEvent.getTarget()).getSelection().asString();
		Clients.showNotification(message);
	}
	
	@Listen("onClick = #open")
	public void open(MouseEvent event) {
		dialog.setVisible(true);
		dialog.setPosition("center");
		CellMouseEvent cellMouseEvent = (CellMouseEvent)myContext.getAttribute("event");
		String message = ((Spreadsheet)cellMouseEvent.getTarget()).getSelection().asString();
		content.setValue(message);
	}
	
	@Listen("onClose = #dialog")
	public void hideDialog(Event event){
		dialog.setVisible(false);
		event.stopPropagation();
	}
	
	@Listen("onClick = #clear")
	public void clear() throws Exception{
		CellMouseEvent cellMouseEvent = (CellMouseEvent)myContext.getAttribute("event");
		Spreadsheet ss = (Spreadsheet)cellMouseEvent.getTarget();
		AuxActionEvent event = new AuxActionEvent(Events.ON_AUX_ACTION, ss, ss.getSelectedSheet(),
				AuxAction.CLEAR_ALL.toString(), ss.getSelection(), new HashMap());
		((EventListener)ss.getUserActionManager()).onEvent(event);
	}
}



