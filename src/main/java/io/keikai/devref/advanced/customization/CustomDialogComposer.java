package io.keikai.devref.advanced.customization;

import io.keikai.ui.*;
import io.keikai.ui.impl.DefaultUserActionManagerCtrl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;

/**
 * To demonstrate how to customize data validation dialog
 * @author Hawk
 *
 */
@SuppressWarnings("serial")
public class CustomDialogComposer extends SelectorComposer<Component> {
	
	@Wire
	private Spreadsheet ss;

	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		UserActionManager actionManager = ss.getUserActionManager();
		//replace the existing handlers
		actionManager.setHandler(
				DefaultUserActionManagerCtrl.Category.AUXACTION.getName(),
				AuxAction.DATA_VALIDATION.getAction(), new MyValidationHandler());
	}
}



