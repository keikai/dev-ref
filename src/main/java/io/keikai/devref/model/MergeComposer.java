package io.keikai.devref.model;

import io.keikai.api.*;
import io.keikai.ui.Spreadsheet;
import io.keikai.ui.event.CellSelectionEvent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.Label;

/**
 * Demonstrate merge API usage
 * 
 * @author Hawk
 * 
 */
@SuppressWarnings("serial")
public class MergeComposer extends SelectorComposer<Component> {

	@Wire
	private Label cellRef;
	@Wire
	private Spreadsheet ss;

	@Listen("onClick = #mergeCenterButton")
	public void mergeCenter() {
		CellOperationUtil.toggleMergeCenter(getSelectedRange());
	}
	
	@Listen("onClick = #mergeAcrossButton")
	public void mergeAcross() {
		CellOperationUtil.merge(getSelectedRange(), true);
	}
	
	@Listen("onClick = #mergeButton")
	public void merge() {
		CellOperationUtil.merge(getSelectedRange(), false);
	}
	
	@Listen("onClick = #unmergeButton")
	public void unmerge() {
		CellOperationUtil.unmerge(getSelectedRange());
	}
	
	private Range getSelectedRange(){
		return Ranges.range(ss.getSelectedSheet(), ss.getSelection());
	}

	@Listen("onCellSelection = #ss")
	public void updateCellReference(CellSelectionEvent event){
		
		cellRef.setValue(Ranges.getAreaRefString(event.getSheet(), ss.getSelection()));
	}
}
