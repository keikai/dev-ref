package io.keikai.devref;

import io.keikai.api.model.Book;
import io.keikai.devref.util.BookUtil;
import io.keikai.ui.Spreadsheet;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;

/**
 * @author Hawk
 */
@SuppressWarnings("serial")
public class CopySheetComposer extends SelectorComposer<Component> {

	@Wire
	private Spreadsheet srcSpreadsheet;
	@Wire
	private Spreadsheet targetSpreadsheet;
	
	@Listen("onClick = #copy")
	public void copySheetToNewBook(){
		Book newBook = BookUtil.copySheetToNewBook("newOne", srcSpreadsheet.getSelectedSheet());
		targetSpreadsheet.setBook(newBook);
	}
	
}
