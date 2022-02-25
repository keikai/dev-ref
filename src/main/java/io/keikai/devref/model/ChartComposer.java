package io.keikai.devref.model;

import io.keikai.api.*;
import io.keikai.api.model.Chart;
import io.keikai.api.model.Chart.*;
import io.keikai.ui.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zul.*;

/**
 * Demonstrate chart related API usage
 * 
 * @author Hawk
 * 
 */
@SuppressWarnings("serial")
public class ChartComposer extends SelectorComposer<Component> {

	private static final AreaRef DEFAULT_DATA = new AreaRef("A1:B6");
	@Wire
	private Intbox toRowBox;
	@Wire
	private Intbox toColumnBox;
	@Wire
	private Spreadsheet ss;
	@Wire
	private Listbox chartListbox;

	private ListModelList<Chart> chartList = new ListModelList<Chart>();


	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		chartListbox.setModel(chartList);
	}


	@Listen("onClick = #addButton")
	public void addByUtil(){
		AreaRef selection = ss.getSelection();
		if (isOneCell(selection)){
			selection = DEFAULT_DATA;
		}
		SheetOperationUtil.addChart(Ranges.range(ss.getSelectedSheet(), selection),
				Type.PIE, Grouping.STANDARD, LegendPosition.RIGHT);
		refreshChartList();
	}

	private boolean isOneCell(AreaRef areaRef) {
		return (areaRef.getLastColumn() == areaRef.getColumn()) && (areaRef.getLastRow()==areaRef.getRow());
	}

	@Listen("onClick = #moveButton")
	public void moveByUtil(){
		if (chartListbox.getSelectedItem() != null){
			Chart chart = chartListbox.getSelectedItem().getValue();
			SheetOperationUtil.moveChart(Ranges.range(ss.getSelectedSheet()),
					chart,
					toRowBox.getValue(), toColumnBox.getValue());
			refreshChartList();
		}
	}
	
	@Listen("onClick = #deleteButton")
	public void deleteByUtil(){
		if (chartListbox.getSelectedItem() != null){
			SheetOperationUtil.deleteChart(Ranges.range(ss.getSelectedSheet()), 
					chartListbox.getSelectedItem().getValue());
			refreshChartList();
		}
	}

	@Listen("onClick = #find")
	public void findBySelection(){
		for (Chart chart : ss.getSelectedSheet().getCharts()){
			AreaRef selection = ss.getSelection();
			AreaRefWithType selectionArea = new AreaRefWithType(selection.getRow(), selection.getColumn(),
					selection.getLastRow(), selection.getLastColumn(), CellSelectionType.ALL);

			SheetAnchor anchor = chart.getAnchor();
			AreaRefWithType chartArea = new AreaRefWithType(anchor.getRow(), anchor.getColumn(),
					anchor.getLastRow(), anchor.getLastColumn(), CellSelectionType.ALL);
			if (selectionArea.overlap(chartArea)){
				chartList.addToSelection(chart);
				return;
			}
		}
		Notification.show("no chart found in the selection");
	}

	public void add() {
		Range dataRange = Ranges.range(ss.getSelectedSheet(),new AreaRef("A1:B6"));
		SheetAnchor selectionAnchor = SheetOperationUtil.toChartAnchor(dataRange);
		Chart chart = dataRange.addChart(selectionAnchor, Type.PIE, Grouping.STANDARD, LegendPosition.RIGHT);
		refreshChartList();
	}


	public void delete() {
		if (chartListbox.getSelectedItem() != null){
			Ranges.range(ss.getSelectedSheet())
					.deleteChart((Chart)chartListbox.getSelectedItem().getValue());
			refreshChartList();
		}
	}

	private void refreshChartList(){
		chartList.clear();
		chartList.addAll(ss.getSelectedSheet().getCharts());
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
}
