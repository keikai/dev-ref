package io.keikai.devref.advanced;

import io.keikai.api.*;
import io.keikai.api.model.Sheet;
import io.keikai.api.model.CellStyle.*;
import io.keikai.ui.Spreadsheet;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zk.ui.util.Clients;

/**
 * This class demonstrates performance tricks.
 * @author Hawk
 *
 */
@SuppressWarnings("serial")
public class PerformanceComposer extends SelectorComposer<Component> {
	

	@Wire
	private Spreadsheet ss;
	
	private static final int COLUMN_SIZE = 20;
	private static final int ROW_SIZE = 16000;
	
	@Listen("onClick = button[label='Fill with autorefresh=true']")
	public void load(Event e){
		Events.echoEvent("onLoadDataAutoRefresh", ss, null);
		Clients.showBusy("populating");
	}
	
	@Listen("onLoadDataAutoRefresh= #ss")
	public void onLoadDataAutoRefresh(Event e) throws InterruptedException{
		loadDataAutoRefresh();
		Clients.clearBusy();
	}
	
	@Listen("onClick = button[label='Fill with autorefresh=false']")
	public void loadWithAutoRefresh(Event e){
		Events.echoEvent("onLoadData", ss, null);
		Clients.showBusy("populating");
	}
	
	@Listen("onLoadData= #ss")
	public void onLoadData(Event e) throws InterruptedException{
		loadData();
		Clients.clearBusy();
	}
	
	private void loadData() {
		Sheet sheet = ss.getSelectedSheet();
		for (int column  = 0 ; column < COLUMN_SIZE ; column++){
			for (int row = 20 ; row < ROW_SIZE ; row++ ){
				Range range = Ranges.range(sheet, row, column);
				range.setAutoRefresh(false);
				range.getCellData().setEditText(row+", "+column);
//				CellOperationUtil.applyFontColor(range, "#0099FF");
//				CellOperationUtil.applyAlignment(range, Alignment.CENTER);
			}
		}
		Ranges.range(ss.getSelectedSheet(), 0, 0, ROW_SIZE, COLUMN_SIZE).notifyChange();
//		Ranges.range(ss.getSelectedSheet()).notifyChange(); // might make a sheet blank for a moment
	}

	private void loadDataAutoRefresh() {
		Sheet sheet = ss.getSelectedSheet();
		for (int column  = 0 ; column < COLUMN_SIZE ; column++){
			for (int row = 0 ; row < ROW_SIZE ; row++ ){
				Range range = Ranges.range(sheet, row, column);
				range.getCellData().setEditText(row+", "+column);
				CellOperationUtil.applyFontColor(range, "#0099FF");
				CellOperationUtil.applyAlignment(range, Alignment.CENTER);
			}
		}
	}
}



