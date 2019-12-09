package io.keikai.devref.advanced.db;

import io.keikai.api.Ranges;
import io.keikai.api.Range.*;
import io.keikai.api.model.Sheet;
import io.keikai.model.impl.AbstractTableAdv;
import io.keikai.ui.*;
import io.keikai.ui.event.StopEditingEvent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.Map;

public class DatabaseComposer extends SelectorComposer<Component> {

	private MyDataService dataService = new MyDataService();
	@Wire
	private Spreadsheet ss;
	@Wire
	private Grid tableGrid;
	@Wire("#load")
	private Button loadButton;

	private Map<Integer, Trade> tradeMap;
	//column index
	public static int ID = 0;
	public static int TYPE = 1;
	public static int SALESPERSON = 2;
	public static int SALES = 3;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		reload();
		Clients.showNotification("the data is loaded from the db");
	}
	
	private void load(Trade trade, int row) {
		Sheet sheet = ss.getSelectedSheet();
		Ranges.range(sheet, row, ID).setCellValue(trade.getId());
		Ranges.range(sheet, row, TYPE).setCellValue(trade.getType());
		Ranges.range(sheet, row, SALESPERSON).setCellValue(trade.getSalesPerson());
		Ranges.range(sheet, row, SALES).setCellEditText(Integer.toString(trade.getSale()));
	}
	
	/**
	 * extract data in a cell into a {@link Trade}
	 * @param row
	 */
	private Trade extract(int row ){
		Sheet sheet = ss.getSelectedSheet();
		Trade trade = new Trade(Ranges.range(sheet, row, ID).getCellData().getDoubleValue().intValue());
		trade.setType(Ranges.range(sheet, row, TYPE).getCellEditText());
		trade.setSalesPerson(Ranges.range(sheet, row, SALESPERSON).getCellEditText());
		trade.setSale(Ranges.range(sheet, row, SALES).getCellData().getDoubleValue().intValue());
		return trade;
	}
	
	@Listen("onClick = #load")
	public void load(){
		reload();
	}
	
	@Listen("onClick = #save")
	public void save(){
		dataService.save(tradeMap);
		tableGrid.setModel(new ListModelList<Trade>(dataService.queryAll().values()));
		loadButton.setDisabled(false);
	}
	
	@Listen("onClick = #add")
	public void add(){
		// modify the model
		Trade trade = new Trade(dataService.nextId());
		tradeMap.put(trade.getId(), trade);
		// update the view
		int maxRow = ss.getMaxVisibleRows()+1;
		ss.setMaxVisibleRows(maxRow);
		load(trade, maxRow-1);
		//extend table row range
		((AbstractTableAdv)ss.getBook().getSheetAt(0).getInternalSheet().getTables().get(0)).insertRows(0, maxRow);
		loadButton.setDisabled(false);
		
	}
	
	@Listen("onClick = #delete")
	public void delete(){
		int row = ss.getSelection().getRow();
		if (isEditable(row)){
			Trade trade = extract(row);
			tradeMap.remove(trade.getId());

			Ranges.range(ss.getSelectedSheet(), ss.getSelection()).toRowRange().delete(DeleteShift.UP);
			int maxVisibleRow = ss.getMaxVisibleRows();
			if (maxVisibleRow > 1){
				ss.setMaxVisibleRows(maxVisibleRow-1);
			}
		}
		loadButton.setDisabled(false);
	}
	
	/**
	 * When onStopEditing fired, the cell value in the book model is still not updated. So we don't extract cell data into a Trade here.
	 * @param event
	 */
	@Listen("onStopEditing = #ss")
	public void update(StopEditingEvent event){
		//validate input with Excel validation
		Events.postEvent("onAfterEditing", ss, event.getRow());
	}
	

	@Listen("onAfterEditing = #ss")
	public void update(Event event){
		Integer row = (Integer)event.getData();
		//the header row is locked
		Trade trade = extract(row);
		tradeMap.put(trade.getId(), trade);
		loadButton.setDisabled(false);
	}

	/**
	 * load data to the sheet
	 */
	public void reload(){
		ss.setSrc(null);
		ss.setSrc("/WEB-INF/books/tradeTemplate.xlsx");
		int row = 1;
		tradeMap = dataService.queryAll();
		for (Map.Entry<Integer, Trade> entry : tradeMap.entrySet()){
			load(entry.getValue(), row);
			row++;
		}
		ss.setMaxVisibleRows(tradeMap.size()+1);
		
		tableGrid.setModel(new ListModelList<Trade>(tradeMap.values()));

		hideAddColumnRowButton();
		applyAccessPolicy();
		loadButton.setDisabled(true);
	}
	
	private void applyAccessPolicy() {
		ss.disableUserAction(AuxAction.PROTECT_SHEET, true);
		ss.disableUserAction(AuxAction.ADD_SHEET, true);
		ss.disableUserAction(AuxAction.DELETE_SHEET, true);
		ss.disableUserAction(AuxAction.HIDE_SHEET, true);
		ss.disableUserAction(AuxAction.COPY_SHEET, true);
		Ranges.range(ss.getSelectedSheet()).protectSheet("", true, true, false, false, false, false, false, false, false, true, false, true, false, false, false);
	}

	
	private boolean isEditable(int rowIndex){
		return rowIndex > 0;
	}

	private void hideAddColumnRowButton() {
		Clients.evalJavaScript("jq('.zstbtn-addCol').hide();jq('.zstbtn-addRow').hide();");
	}
}
