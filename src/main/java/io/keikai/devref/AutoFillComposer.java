package io.keikai.devref;

import io.keikai.api.*;
import io.keikai.ui.Spreadsheet;
import io.keikai.api.Range.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.*;

/**
 * Demonstrate CellStyle, CellOperationUtil API usage
 * 
 * @author Hawk
 * 
 */
@SuppressWarnings("serial")
public class AutoFillComposer extends SelectorComposer<Component> {

	@Wire
	private Listbox fillTypeBox;
	@Wire
	private Intbox cellCountBox;
	@Wire
	private Spreadsheet ss;

	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		fillTypeBox.setModel(getSupportedFillType());
	}

	@Listen("onClick = #fillButton")
	public void autoFill() {
		AreaRef selection = ss.getSelection();
		Range src = Ranges.range(ss.getSelectedSheet(), selection.getRow(),
				selection.getColumn(), selection.getLastRow(), selection.getLastColumn());
		Range dest = Ranges.range(ss.getSelectedSheet(), selection.getRow(),
				selection.getColumn(), selection.getLastRow(),
				selection.getLastColumn() + cellCountBox.getValue());
		CellOperationUtil.autoFill(src, dest, 
				(AutoFillType) fillTypeBox.getSelectedItem().getValue());
	}
	
	private ListModelList<AutoFillType> getSupportedFillType(){
		ListModelList<Range.AutoFillType> list = new ListModelList<Range.AutoFillType>();
		list.add(AutoFillType.DEFAULT);
		list.add(AutoFillType.COPY);
		list.add(AutoFillType.FORMATS);
		list.add(AutoFillType.VALUES);
		list.addToSelection(AutoFillType.DEFAULT);
		return list;
	}

}
