package io.keikai.devref.events;

import io.keikai.ui.event.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.Menupopup;

/**
 * Demonstrate header events.
 * @author Hawk
 *
 */
@SuppressWarnings("serial")
public class MouseEventsComposer extends SelectorComposer<Component> {

	@Wire
	private Menupopup topHeaderMenu;
	@Wire
	private Menupopup leftHeaderMenu;
	
	@Listen(Events.ON_HEADER_RIGHT_CLICK + "= spreadsheet")
	public void onHeaderRightClick(HeaderMouseEvent event) {
		
		switch(event.getType()){
		case COLUMN:
			topHeaderMenu.open(event.getClientx(),  event.getClienty());
			break;
		case ROW:
			leftHeaderMenu.open(event.getClientx(),  event.getClienty());
			break;
		}
	}
}
