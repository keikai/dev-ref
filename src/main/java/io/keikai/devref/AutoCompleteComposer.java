package io.keikai.devref;

import io.keikai.api.Ranges;
import io.keikai.devref.advanced.SpreadsheetCellPopup;
import io.keikai.ui.Spreadsheet;
import io.keikai.ui.event.StartEditingEvent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.*;

import java.util.*;


/**
 * @author Hawk
 * 
 */
public class AutoCompleteComposer extends SelectorComposer<Component> {

	@Wire
	private Combobox box;
	@Wire
	private SpreadsheetCellPopup inputPopup;
	@Wire
	private Spreadsheet ss;

	ListModelList<Locale> modelList = new ListModelList<Locale>(Arrays.asList(Locale.getAvailableLocales()));

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		// fill the data model with data source (e.g. from a database)
		box.setModel(ListModels.toListSubModel(modelList));
	}

	@Listen("onStartEditing = #ss")
	public void showInputPopup(StartEditingEvent event) {
		if (event.getColumn() == 0) {
			event.cancel();
			inputPopup.openAtCell(ss, event.getRow(), event.getColumn(), "overlap");
			box.setValue(event.getClientValue().toString());
			box.setFocus(true);
		}
	}

	@Listen("onSelect= #box")
	public void endInput(){
		inputPopup.close();
		Ranges.range(ss.getSelectedSheet(), ss.getSelection()).setCellEditText(box.getValue());
		ss.focus();
	}


}
