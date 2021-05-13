package io.keikai.devref.model;

import io.keikai.api.*;
import io.keikai.api.model.Sheet;
import io.keikai.ui.Spreadsheet;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;

/**
 * Demonstrate copy & cut , CellOperationUtil API usage
 * 
 * @author Hawk
 * 
 */
@SuppressWarnings("serial")
public class CopyCutComposer extends SelectorComposer<Component> {

	@Wire
	private Spreadsheet ss;


	@Listen("onClick = #copyButton")
	public void copyByUtil() {
		Range src = Ranges.range(ss.getSelectedSheet(), ss.getSelection());
		Range dest = Ranges.range(getDestinationSheet(), ss.getSelection());
		CellOperationUtil.paste(src, dest);
	}


	@Listen("onClick = #cutButton")
	public void cutByUtil() {
		Range src = Ranges.range(ss.getSelectedSheet(), ss.getSelection());
		Range dest = Ranges.range(getDestinationSheet(), ss.getSelection());
		CellOperationUtil.cut(src, dest);
	}

	private Sheet getDestinationSheet(){
		return ss.getBook().getSheetAt(1);
	}

	//demonstrate Range API usage
	public void copy(){
		Range src = Ranges.range(ss.getSelectedSheet(), ss.getSelection());
		Range destination = Ranges.range(getDestinationSheet(), ss.getSelection());
		src.paste(destination);
	}
}
