package io.keikai.devref.advanced.customization;

import io.keikai.ui.event.CellMouseEvent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.Menupopup;

/**
 * This class demonstrate how to open a new custom context menu.
 * @author Hawk
 *
 */
@SuppressWarnings("serial")
public class CustomContextMenuComposer extends SelectorComposer<Component> {

	@Wire
	private Menupopup myContext;

	@Listen("onCellRightClick = #ss")
	public void doContext(CellMouseEvent event) {
		myContext.open(event.getClientx(), event.getClienty());
		myContext.setAttribute("event", event);
	}
}



