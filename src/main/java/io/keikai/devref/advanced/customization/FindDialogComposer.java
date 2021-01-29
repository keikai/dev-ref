package io.keikai.devref.advanced.customization;

import io.keikai.api.*;
import io.keikai.api.model.*;
import io.keikai.api.model.CellData.CellType;
import io.keikai.ui.Spreadsheet;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zul.*;

/**
 * @author Hawk
 */
@SuppressWarnings("serial")
public class FindDialogComposer extends SelectorComposer<Component> {
	@Wire
	protected Window findDialog;
	@Wire
	protected Textbox keywordBox;
	@Wire
	protected Combobox scopeBox;
	@Wire
	protected Checkbox caseSensitiveBox;
	@Wire
	protected Checkbox entireCellBox;
	
	protected enum SearchScope {Sheet, Book};
	protected ListModelList<SearchScope> scopeList = new ListModelList<SearchScope>(SearchScope.values());

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		scopeBox.setModel(scopeList);
		scopeList.addToSelection(scopeList.get(0));
	}
	
	@Listen("onClick=#find; onOK=#keywordBox")
	public void find(Event event) {
		Spreadsheet ss = (Spreadsheet)findDialog.getAttribute(Spreadsheet.class.toString());
		Range targetCell = null;
		if (scopeList.getSelection().iterator().next().equals(SearchScope.Sheet)){
			targetCell = findNext(ss.getSelectedSheet(), Ranges.range(ss.getSelectedSheet(), ss.getSelection()));
		}else{
			targetCell = findNext(ss.getBook(), ss.getSelectedSheet(), Ranges.range(ss.getSelectedSheet(), ss.getSelection()));
		}
		;
		if (found(targetCell)){
			focusToFoundCell(ss, targetCell);
		}else{
			Notification.show("Not found");
		}

		keywordBox.setFocus(true); //get focus back, so users can "find" without putting focus back after each find
	}


	@Listen(Events.ON_CLICK + " = #close")
	public void close(MouseEvent event) {
		findDialog.setVisible(false);
	}

	@Listen("onOpen = #findDialog")
	public void show(){
		findDialog.setVisible(true);
		keywordBox.setFocus(true);
	}

	private boolean found(Range targetCell) {
		return targetCell != null;
	}


	protected void focusToFoundCell(Spreadsheet ss, Range targetCell) {
		if (!ss.getSelectedSheet().getSheetName().equals(targetCell.getSheetName())) {
			ss.setSelectedSheet(targetCell.getSheetName());
		}
		ss.focusTo(targetCell.getRow(), targetCell.getColumn());
	}

	/**
	 * @return null if it's not found
	 */
	protected Range findNext(Book book, Sheet currentSheet, Range currentSelection) {
		Range foundCell = null;
		int index = book.getSheetIndex(currentSheet);
		while (index < book.getNumberOfSheets()){
			Range resultCell = findNext(book.getSheetAt(index), currentSelection);
			if (found(resultCell)){
				foundCell = resultCell;
				break;
			}else{ //nothing matched, move to next sheet
				index++;
			}
		}
		return foundCell;
	}
	
	/**
	 * Start finding from the next cell in the same row of the current selection. Left to right, top to down.
	 * When reaching the end of a sheet, do not find from the beginning.
	 * @return null if it's not found
	 */
	protected Range findNext(Sheet sheet, Range currentSelection) {
		int lastColumn = Ranges.range(sheet).getDataRegion().getLastColumn();
		int lastRow = Ranges.range(sheet).getDataRegion().getLastRow();
		String keyword = keywordBox.getValue().trim();
		int row = getStartingRow(sheet, currentSelection);
		int column = getStartingColumn(sheet, currentSelection); 
		while (row <= lastRow){
			while (column <= lastColumn){
				Range cell = Ranges.range(sheet, row, column);
				if (cell.getCellData().getType() == CellType.STRING){
					if (match(cell.getCellData().getEditText(), keyword)){
						return cell;
					}
				}
				column++;
			}
			column = 0;
			row++;
		}
		return null;
	}
	
	protected int getStartingRow(Sheet sheet, Range selection){
		return sheet.equals(selection.getSheet()) ? selection.getRow() : 0; 
	}
	/**
	 * Start from the next cell by rows in the same sheet
	 * @return
	 */
	protected int getStartingColumn(Sheet sheet, Range selection){
		return sheet.equals(selection.getSheet()) ? selection.getColumn()+1 : 0; 
	}

	protected boolean match(String cellEditText, String keyword) {
		String content = cellEditText;
		if (!caseSensitiveBox.isChecked()){
			content = content.toLowerCase();
		}
		if (entireCellBox.isChecked()){
			return content.equals(keyword);
		}else{
			return content.contains(keyword);
		}
	}
}



