package io.keikai.devref.model;

import io.keikai.api.*;
import io.keikai.api.model.*;
import io.keikai.devref.util.ClientUtil;
import io.keikai.ui.Spreadsheet;
import io.keikai.ui.event.Events;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

import java.util.Objects;

/**
 * Demonstrate cell data API usage
 * @author dennis
 *
 */
@SuppressWarnings("serial")
public class CellDataComposer extends SelectorComposer<Component> {

	@Wire
	private Label cellType;
	@Wire
	private Label cellFormatText;
	@Wire
	private Label cellEditText;
	@Wire
	private Label cellValue;
	@Wire
	private Label cellResultType;
	@Wire
	private Label cellRef;
	@Wire
	private Textbox cellEditTextBox;
	@Wire
	private Spreadsheet ss;
	
	@Wire
	Textbox cellFormatTextBox;
	
	
	@Listen(Events.ON_CELL_FOCUS + " = #ss")
	public void onCellFocus(){
		CellRef pos = ss.getCellFocus();
		
		refreshCellInfo(pos.getRow(),pos.getColumn());		
	}
	
	private void refreshCellInfo(int row, int col){
		Range range = Ranges.range(ss.getSelectedSheet(),row,col);
		
		cellRef.setValue(Ranges.getCellRefString(row, col));
		//show a cell's data
		CellData data = range.getCellData();
		cellFormatText.setValue(data.getFormatText());
		cellEditText.setValue(data.getEditText());
		cellType.setValue(data.getType().toString());
		
		Object value = data.getValue();
		cellValue.setValue(value==null?"null":(value.getClass().getSimpleName()+" : "+value));
		cellResultType.setValue(data.getResultType().toString());
		
		cellEditTextBox.setValue(data.getEditText());
		
		cellFormatTextBox.setValue(range.getCellStyle().getDataFormat());
	}
	
	@Listen("onChange = #cellEditTextBox")
	public void onEditboxChange(){
		CellRef pos = ss.getCellFocus();
		Range range = Ranges.range(ss.getSelectedSheet(),pos.getRow(),pos.getColumn());
		CellData data = range.getCellData();
		if(data.validateEditText(cellEditTextBox.getValue())){
			try{
				data.setEditText(cellEditTextBox.getValue());
			}catch (IllegalFormulaException e){
				//handle illegal formula input
			}
		}else{
			ClientUtil.showWarn("not a valid value");
		}
		refreshCellInfo(pos.getRow(),pos.getColumn());
		
	}
	
	@Listen("onChange = #cellFormatTextBox")
	public void onFormatboxChange(){
		Range selection = Ranges.range(ss.getSelectedSheet(), ss.getSelection());;
		CellStyle oldStyle = selection.getCellStyle();
		String format = cellFormatTextBox.getValue();
		if (Objects.equals(oldStyle.getDataFormat(), format)) {
			return;
		}
		//don't edit old style directly because it might be shared with other cell
		CellStyle newStyle = selection.getCellStyleHelper().builder(oldStyle)
				.dataFormat(format).build();
		selection.setCellStyle(newStyle);
		selection.notifyChange();
		refreshCellInfo(selection.getRow(),selection.getColumn());
	}
}



