package io.keikai.devref.advanced.customization;

import io.keikai.devref.util.BookUtil;
import io.keikai.ui.*;
import io.keikai.ui.impl.DefaultUserActionManagerCtrl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;


/**
 * @author Hawk
 *
 */
public class CustomToolbarComposer extends SelectorComposer<Component> {

	private static final String ACTION_KEY = "attach-file";
	@Wire
	private Spreadsheet spreadsheet;

	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		spreadsheet.setBook(BookUtil.newBook());
		addToolbarbutton();
		spreadsheet.removeToolbarButton(AuxAction.EXPORT_PDF);
	}

	private void addToolbarbutton() {
		spreadsheet.addToolbarButton(ToolbarButton.Builder.create(ACTION_KEY).withTooltip("attach file").withLabel("attach").build());
		spreadsheet.getUserActionManager().registerHandler(DefaultUserActionManagerCtrl.Category.AUXACTION.getName(), ACTION_KEY, new AttachFileHandler());
	}
}



