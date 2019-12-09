package io.keikai.devref.advanced.customization;

import io.keikai.api.*;
import io.keikai.api.model.*;
import io.keikai.api.model.CellData.CellType;
import io.keikai.ui.Spreadsheet;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
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
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		ListModelList<SearchScope> scopeList = new ListModelList<SearchScope>(SearchScope.values());
		scopeBox.setModel(scopeList);
		scopeList.addToSelection(scopeList.get(0));
	}
	
	@Listen("onClick=#find; onOK=#keywordBox")
	public void find(Event event) {
		Spreadsheet ss = (Spreadsheet)findDialog.getAttribute(Spreadsheet.class.toString());
		Range targetCell = null;
		if (scopeBox.getSelectedItem().getValue().equals(SearchScope.Sheet)){
			targetCell = findNext(ss.getSelectedSheet(), Ranges.range(ss.getSelectedSheet(), ss.getSelection()));
		}else{
			targetCell = findNext(ss.getBook(), ss.getSelectedSheet(), Ranges.range(ss.getSelectedSheet(), ss.getSelection()));
		}
		focusToCell(ss, targetCell);
	}


	protected void focusToCell(Spreadsheet ss, Range targetCell) {
		ss.setSelectedSheet(targetCell.getSheetName());
		ss.focusTo(targetCell.getRow(), targetCell.getColumn());
	}
	
	protected Range findNext(Book book, Sheet currentSheet, Range currentSelection) {
		Range foundCell = currentSelection; 
		int index = book.getSheetIndex(currentSheet);
		while (index < book.getNumberOfSheets()){
			Range resultCell = findNext(book.getSheetAt(index), currentSelection);
			if (resultCell.getSheet().equals(currentSelection.getSheet()) &&
				currentSelection.getRow() == resultCell.getRow() &&
				currentSelection.getColumn() == resultCell.getColumn()){ //nothing matched, move to next sheet
				index++;
			}else{
				foundCell = resultCell;
				break;
			}
		}
		return foundCell;
	}
	
	/**
	 * Start finding from the next cell by rows of the current selection. If nothing found, return the current selection.
	 * When reach the end of a sheet, do not find from the beginning.
	 * @param sheet
	 * @param currentSelection
	 * @return
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
		return currentSelection;
	}
	
	protected int getStartingRow(Sheet sheet, Range selection){
		return sheet.equals(selection.getSheet()) ? selection.getRow() : 0; 
	}
	/**
	 * Start from the next cell by rows in the same sheet
	 * @param sheet
	 * @param selection
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

	@Listen("onClick = #close")
	public void close(MouseEvent event) {
		findDialog.setVisible(false);
	}
	
	@Listen("onOpen = #findDialog")
	public void show(){
		findDialog.setVisible(true);
		keywordBox.setFocus(true);
	}
}



