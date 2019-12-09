package io.keikai.devref;

import io.keikai.api.*;
import io.keikai.api.model.*;
import io.keikai.ui.Spreadsheet;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;

/**
 * Demonstrate cell data API usage
 * @author dennis
 *
 */
@SuppressWarnings("serial")
public class CellHyperlinkComposer extends SelectorComposer<Component> {

	@Wire
	private Label cellFormatText;
	@Wire
	private Label hyperlinkType;
	@Wire
	private Label hyperlinkLabel;
	@Wire
	private Label hyperlinkAddress;
	@Wire
	private Label cellRef;
	@Wire
	private Spreadsheet ss;
	
//	@Wire
//	Textbox cellFormatTextBox;
	
	
	@Listen("onCellFocus = #ss")
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
		
		Hyperlink link = range.getCellHyperlink();
		if(link!=null){
			hyperlinkType.setValue(link.getType().toString());
			hyperlinkLabel.setValue(link.getLabel());
			hyperlinkAddress.setValue(link.getAddress());
		}else{
			hyperlinkType.setValue("");
			hyperlinkLabel.setValue("");
			hyperlinkAddress.setValue("");
		}
	}
}



