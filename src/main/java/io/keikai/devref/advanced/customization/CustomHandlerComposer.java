package io.keikai.devref.advanced.customization;

import io.keikai.ui.*;
import io.keikai.ui.impl.DefaultUserActionManagerCtrl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;

/**
 * This class demonstrate usage of "custom user action handler".
 * @author Hawk
 *
 */
public class CustomHandlerComposer extends SelectorComposer<Component> {
	
	@Wire
	private Spreadsheet ss;

	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		//initialize custom handlers
		UserActionManager actionManager = ss.getUserActionManager();
		actionManager.registerHandler(
				DefaultUserActionManagerCtrl.Category.AUXACTION.getName(),
				AuxAction.NEW_BOOK.getAction(), new NewBookHandler());
		actionManager.registerHandler(
				DefaultUserActionManagerCtrl.Category.AUXACTION.getName(),
				AuxAction.SAVE_BOOK.getAction(), new SaveBookHandler());
	}
}



