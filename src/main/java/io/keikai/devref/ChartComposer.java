package io.keikai.devref;

import io.keikai.api.*;
import io.keikai.api.model.Chart;
import io.keikai.api.model.Chart.*;
import io.keikai.ui.Spreadsheet;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

/**
 * Demonstrate chart related API usage
 * 
 * @author Hawk
 * 
 */
@SuppressWarnings("serial")
public class ChartComposer extends SelectorComposer<Component> {

	@Wire
	private Intbox toRowBox;
	@Wire
	private Intbox toColumnBox;
	@Wire
	private Spreadsheet ss;
	@Wire
	private Listbox chartListbox;

	private ListModelList<Chart> chartList = new ListModelList<Chart>();

	
	public void add() {
		Range dataRange = Ranges.range(ss.getSelectedSheet(),new AreaRef("A1:B6"));
		SheetAnchor selectionAnchor = SheetOperationUtil.toChartAnchor(dataRange);
		dataRange.addChart(selectionAnchor, Type.PIE, Grouping.STANDARD, LegendPosition.RIGHT);
		refreshChartList();
	}


	public void delete() {
		if (chartListbox.getSelectedItem() != null){
			Ranges.range(ss.getSelectedSheet())
				.deleteChart((Chart)chartListbox.getSelectedItem().getValue());
			refreshChartList();
		}
	}

	public void move() {
		if (chartListbox.getSelectedItem() != null){
			//calculate destination anchor
			SheetAnchor fromAnchor = ((Chart) chartListbox.getSelectedItem()
					.getValue()).getAnchor();
			int rowOffset = fromAnchor.getLastRow() - fromAnchor.getRow();
			int columnOffset = fromAnchor.getLastColumn() - fromAnchor.getColumn();
			SheetAnchor toAnchor = new SheetAnchor(toRowBox.getValue(), 
					toColumnBox.getValue(),
					fromAnchor.getXOffset(), fromAnchor.getYOffset(),
					toRowBox.getValue()+rowOffset, toColumnBox.getValue()+columnOffset,
					fromAnchor.getLastXOffset(), fromAnchor.getLastYOffset());
			
			Ranges.range(ss.getSelectedSheet())
				.moveChart(toAnchor, (Chart)chartListbox.getSelectedItem().getValue());
			refreshChartList();
		}
	}

	private void refreshChartList(){
		chartList.clear();
		chartList.addAll(ss.getSelectedSheet().getCharts());
		chartListbox.setModel(chartList);
	}
	
	@Listen("onClick = #addButton")
	public void addByUtil(){
		SheetOperationUtil.addChart(Ranges.range(ss.getSelectedSheet(),new AreaRef("A1:B6")),
		Type.PIE, Grouping.STANDARD, LegendPosition.RIGHT);
		refreshChartList();
	}
	
	@Listen("onClick = #moveButton")
	public void moveByUtil(){
		if (chartListbox.getSelectedItem() != null){
			SheetOperationUtil.moveChart(Ranges.range(ss.getSelectedSheet()),
					(Chart)chartListbox.getSelectedItem().getValue(),
					toRowBox.getValue(), toColumnBox.getValue());
			refreshChartList();
		}
	}
	
	@Listen("onClick = #deleteButton")
	public void deleteByUtil(){
		if (chartListbox.getSelectedItem() != null){
			SheetOperationUtil.deleteChart(Ranges.range(ss.getSelectedSheet()), 
					(Chart)chartListbox.getSelectedItem().getValue());
			refreshChartList();
		}
	}
}
