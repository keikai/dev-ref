package io.keikai.devref.advanced;

import io.keikai.ui.Spreadsheet;
import io.keikai.ui.event.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;

/**
 * An example to implement audit trail.
 * @author Hawk
 */
public class AuditTrailComposer extends SelectorComposer<Component> {

	private static final long serialVersionUID = 1L;
	@Wire
	private Spreadsheet ss;

	@Listen(Events.ON_AFTER_UNDOABLE_MANAGER_ACTION + "= #ss")
	public void record(UndoableActionManagerEvent event){
		//combine a user info with an action as an audit trail
		System.out.println(event.getAction() + "," + event.getType());
	}
}



