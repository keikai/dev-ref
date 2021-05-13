package io.keikai.devref.model;

import io.keikai.api.*;
import io.keikai.api.model.*;
import io.keikai.ui.Spreadsheet;
import io.keikai.api.model.CellStyle.*;
import io.keikai.ui.event.Events;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.*;
import org.zkoss.zul.ext.Selectable;

import java.util.*;

/**
 * Demonstrate CellStyle & CellOperationUtil API usage
 * 
 * @author Hawk
 * 
 */
@SuppressWarnings("serial")
public class CellStyleComposer extends SelectorComposer<Component> {

	@Wire
	private Label cellRef;
	@Wire
	private Label hAlign;
	@Wire
	private Label vAlign;
	@Wire
	private Label tBorder;
	@Wire
	private Label bBorder;
	@Wire
	private Label lBorder;
	@Wire
	private Label rBorder;
	@Wire
	private Listbox hAlignBox;
	@Wire
	private Listbox vAlignBox;
	@Wire
	private Spreadsheet ss;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		hAlignBox.setModel(getAlignmentList());
		vAlignBox.setModel(getVertocalAlignmentList());
	}

	@Listen(Events.ON_CELL_FOCUS + "= #ss")
	public void onCellFocus() {
		CellRef pos = ss.getCellFocus();
		refreshCellStyle(pos.getRow(), pos.getColumn());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void refreshCellStyle(int row, int col) {
		Range range = Ranges.range(ss.getSelectedSheet(), row, col);

		cellRef.setValue(Ranges.getCellRefString(row, col));

		CellStyle style = range.getCellStyle();

		// display cell style
		hAlign.setValue(style.getAlignment().name());
		vAlign.setValue(style.getVerticalAlignment().name());
		tBorder.setValue(style.getBorderTop().name());
		bBorder.setValue(style.getBorderBottom().name());
		lBorder.setValue(style.getBorderLeft().name());
		rBorder.setValue(style.getBorderRight().name());

		// update to editor
		List<Object> selection = new ArrayList<Object>();
		selection.add(style.getAlignment());
		((Selectable) hAlignBox.getModel()).setSelection(selection);
		selection.clear();
		selection.add(style.getVerticalAlignment());
		((Selectable) vAlignBox.getModel()).setSelection(selection);

	}

	@Listen("onSelect = #hAlignBox")
	public void applyAlignmentByUtil() {

		Range selection = Ranges.range(ss.getSelectedSheet(), ss.getSelection());
		CellOperationUtil.applyAlignment(selection
				, (Alignment)hAlignBox.getSelectedItem().getValue());
	}

	@Listen("onSelect = #vAlignBox")
	public void applyVerticalAlignmentByUtil() {

		Range selection = Ranges.range(ss.getSelectedSheet(), ss.getSelection());
		CellOperationUtil.applyVerticalAlignment(selection
				, vAlignBox.getSelectedItem().getValue());
	}

	private ListModelList<Alignment> getAlignmentList() {
		ListModelList<Alignment> list = new ListModelList<Alignment>();
		list.add(Alignment.LEFT);
		list.add(Alignment.CENTER);
		list.add(Alignment.RIGHT);
		list.add(Alignment.GENERAL);

		return list;
	}

	private ListModelList<VerticalAlignment> getVertocalAlignmentList() {
		return new ListModelList<VerticalAlignment>(Arrays.asList(VerticalAlignment
				.values()));
	}

	//demonstrate API usage
	public void applyAlignment() {
		Range selection = Ranges.range(ss.getSelectedSheet(), ss.getSelection());
		CellStyle oldStyle = selection.getCellStyle();
		CellStyle newStyle = selection.getCellStyleHelper().builder(oldStyle)
				.alignment((Alignment)hAlignBox.getSelectedItem().getValue()).build();
		selection.setCellStyle(newStyle);
	}
}
