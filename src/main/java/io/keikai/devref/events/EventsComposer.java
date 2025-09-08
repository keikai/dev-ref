package io.keikai.devref.events;

import io.keikai.api.*;
import io.keikai.api.model.*;
import io.keikai.devref.util.RangeHelper;
import io.keikai.model.impl.ColorImpl;
import io.keikai.ui.Spreadsheet;
import io.keikai.ui.event.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.*;

import java.util.ArrayList;

/**
 * This controller listens to all Spreadsheet's events and show related messages.
 * @author dennis, Hawk
 *
 */
public class EventsComposer extends SelectorComposer<Component>{

	private static final long serialVersionUID = 1L;
	
	private ListModelList<String> eventFilterModel = new ListModelList<String>();
	private ListModelList<String> infoModel = new ListModelList<String>();
	
	@Wire
	Spreadsheet ss;
	@Wire
	private Listbox eventFilterList;
	@Wire
	private Grid infoList;
	private boolean isUndo = false;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		initModel();
	}

	//Events

	@Listen(Events.ON_CTRL_KEY + "= #ss")
	public void onCtrlKey(KeyEvent event){
		StringBuilder info = new StringBuilder();
		
		info.append("Keys : ").append(event.getKeyCode())
			.append(", Ctrl:").append(event.isCtrlKey())
			.append(", Alt:").append(event.isAltKey())
			.append(", Shift:").append(event.isShiftKey());
		
		if(isShowEventInfo(event.getName())){
			addInfo(info.toString());
		}

		if (isUndoPressed(event)){
			isUndo = true;
		}
	}

	// ctrl + z
	private boolean isUndoPressed(KeyEvent event) {
		return event.isCtrlKey() && event.getKeyCode() == 90;
	}

	@Listen(Events.ON_CELL_CLICK + " = #ss")
	public void onCellClick(CellMouseEvent event){
		StringBuilder info = new StringBuilder();
		info.append("Click on cell ")
		.append(Ranges.getCellRefString(event.getRow(),event.getColumn()));
		
		if(isShowEventInfo(event.getName())){
			addInfo(info.toString());
		}
	}
	@Listen(Events.ON_CELL_RIGHT_CLICK+" = #ss")
	public void onCellRightClick(CellMouseEvent event){
		StringBuilder info = new StringBuilder();
		info.append("Right-click on cell ").append(Ranges.getCellRefString(event.getRow(),event.getColumn()));
		
		if(isShowEventInfo(event.getName())){
			addInfo(info.toString());
		}
	}
	@Listen(Events.ON_CELL_DOUBLE_CLICK +" = #ss")
	public void onCellDoubleClick(CellMouseEvent event){
		StringBuilder info = new StringBuilder();
		info.append("Double-click on cell ").append(Ranges.getCellRefString(event.getRow(),event.getColumn()));
		
		if(isShowEventInfo(event.getName())){
			addInfo(info.toString());
		}
	}
	
	
	
	@Listen(Events.ON_HEADER_CLICK +" = #ss")
	public void onHeaderClick(HeaderMouseEvent event){
		StringBuilder info = new StringBuilder();
		info.append("Click on ").append(event.getType()).append(" ");
		
		switch(event.getType()){
		case COLUMN:
			info.append(Ranges.getColumnRefString(event.getIndex()));
			break;
		case ROW:
			info.append(Ranges.getRowRefString(event.getIndex()));
			break;
		}
		
		if(isShowEventInfo(event.getName())){
			addInfo(info.toString());
		}
	}
	@Listen(Events.ON_HEADER_RIGHT_CLICK + " = #ss")
	public void onHeaderRightClick(HeaderMouseEvent event){
		StringBuilder info = new StringBuilder();
		info.append("Right-click on ").append(event.getType()).append(" ");
		
		switch(event.getType()){
		case COLUMN:
			info.append(Ranges.getColumnRefString(event.getIndex()));
			break;
		case ROW:
			info.append(Ranges.getRowRefString(event.getIndex()));
			break;
		}
		
		if(isShowEventInfo(event.getName())){
			addInfo(info.toString());
		}
	}
	@Listen(Events.ON_HEADER_DOUBLE_CLICK + " = #ss")
	public void onHeaderDoubleClick(HeaderMouseEvent event){
		StringBuilder info = new StringBuilder();
		info.append("Double-click on ").append(event.getType()).append(" ");
		
		switch(event.getType()){
		case COLUMN:
			info.append(Ranges.getColumnRefString(event.getIndex()));
			break;
		case ROW:
			info.append(Ranges.getRowRefString(event.getIndex()));
			break;
		}
		
		if(isShowEventInfo(event.getName())){
			addInfo(info.toString());
		}
	}

	@Listen(Events.ON_HEADER_UPDATE + " = #ss")
	public void onHeaderUpdate(HeaderUpdateEvent event){
		StringBuilder info = new StringBuilder();
		info.append("Header ").append(event.getAction())
			.append(" on ").append(event.getType());
		switch(event.getType()){
		case COLUMN:
			info.append(" ").append(Ranges.getColumnRefString(event.getIndex()));
			break;
		case ROW:
			info.append(" ").append(Ranges.getRowRefString(event.getIndex()));
			break;
		}

		switch(event.getAction()){
		case RESIZE:
			if(event.isHidden()){
				info.append(" hides ");
			}else{
				info.append(" changes to ").append(event.getSize());
			}
			break;
		}
		
		if(isShowEventInfo(event.getName())){
			addInfo(info.toString());
		}
	}
	
	@Listen(Events.ON_CELL_FOCUS + " = #ss")
	public void onCellFocus(CellEvent event){
		StringBuilder info = new StringBuilder();
		info.append("Focus on[").append(Ranges.getCellRefString(event.getRow(),event.getColumn())).append("]");
		
		if(isShowEventInfo(event.getName())){
			addInfo(info.toString());
		}
	}
	
	@Listen(Events.ON_CELL_SELECTION + " = #ss") //known issue https://tracker.zkoss.org/browse/KEIKAI-362
	public void onCellSelection(CellSelectionEvent event){
		StringBuilder info = new StringBuilder();
		info.append("Select on[").append(Ranges.getAreaRefString(event.getSheet(), event.getArea())).append("]");
		Ranges.getCellRefString(event.getArea().getLastRow(), event.getLastColumn()); //bottom-right corner
		if(isShowEventInfo(event.getName())){
			addInfo(info.toString());
		}
	}
	
	@Listen(Events.ON_CELL_SELECTION_UPDATE + " = #ss")
	public void onCellSelectionUpdate(CellSelectionUpdateEvent event){
		StringBuilder info = new StringBuilder();
		info.append("Selection update from[")
		.append(Ranges.getAreaRefString(event.getOrigRow(),event.getOrigColumn()
				, event.getOrigLastRow(),event.getOrigLastColumn()))
		.append("] to [")
		.append(Ranges.getAreaRefString(event.getSheet(), event.getArea())).append("]");

		if(isShowEventInfo(event.getName())){
			addInfo(info.toString());
		}
	}

	@Listen(Events.ON_START_EDITING + " = #ss")
	public void onStartEditing(StartEditingEvent event){
		StringBuilder info = new StringBuilder();
		String ref = Ranges.getCellRefString(event.getRow(),event.getColumn());
		info.append("Start editing ").append(ref)
		.append(", editing-value is ").append("\""+event.getEditingValue()+"\"")
		.append(" client-value is ").append("\""+event.getClientValue()+"\"");
		
		if(isShowEventInfo(event.getName())){
			addInfo(info.toString());
		}
		
		if(ref.equals("D1")){
			String newValue = "Surprise!!";
			//we change the editing value
			event.setEditingValue(newValue);
			addInfo("Editing value is change to "+newValue);
		}else if(ref.equals("E1")){
			//forbid editing
			event.cancel();
			addInfo("Editing E1 is canceled");
		}
	}

	@Listen(Events.ON_EDITBOX_EDITING + " = #ss")
	public void onEditboxEditing(EditboxEditingEvent event){
		StringBuilder info = new StringBuilder();
		String ref = Ranges.getCellRefString(event.getRow(),event.getColumn());
		info.append("Editing ").append(ref)
		.append(", value is ").append("\""+event.getEditingValue()+"\"");
		
		if(isShowEventInfo(event.getName())){
			addInfo(info.toString());
		}
	}	
	
	@Listen(Events.ON_STOP_EDITING + " = #ss")
	public void onStopEditing(StopEditingEvent event){
		StringBuilder info = new StringBuilder();
		String ref = Ranges.getCellRefString(event.getRow(),event.getColumn());
		info.append("Stop editing ").append(ref)
		.append(", current value is "+Ranges.range(event.getSheet(), event.getRow(), event.getColumn()))
		.append(", editing-value is ").append("\""+event.getEditingValue()+"\"");
		
		if(isShowEventInfo(event.getName())){
			addInfo(info.toString());
		}
		
		if(ref.equals("D3")){
			String newValue = event.getEditingValue()+"-Woo";
			//we change the editing value
			event.setEditingValue(newValue);
			addInfo("Editing value is changed to \""+newValue+"\"");
		}else if(ref.equals("E3")){
			//forbid editing
			event.cancel();
			addInfo("Editing E3 is canceled");
		}
	}
	
	@Listen(Events.ON_AFTER_CELL_CHANGE + " = #ss")
	public void onAfterCellChange(CellAreaEvent event){
		StringBuilder info = new StringBuilder();

		info.append(String.format("%s %s", event.getName(), Ranges.getAreaRefString(event.getSheet(), event.getArea())));
		info.append(", value is \""
		+Ranges.range(event.getSheet(),event.getArea()).getCellFormatText()+"\"");

		if (isUndo){
			info.append(", changed by Undo");
			isUndo = false;
		}
		if(isShowEventInfo(event.getName())){
			addInfo(info.toString());
		}
	}	
	
	@Listen(Events.ON_CELL_HYPERLINK + " = #ss")
	public void onCellHyperlink(CellHyperlinkEvent event){
		StringBuilder info = new StringBuilder();
		
		info.append("Hyperlink ").append(event.getType())
			.append(" on : ")
			.append(Ranges.getCellRefString(event.getRow(),event.getColumn()))
			.append(", address : ").append(event.getAddress());
		
		if(isShowEventInfo(event.getName())){
			addInfo(info.toString());
		}
	}		

	

	
	@Listen(Events.ON_AFTER_SHEET_CREATE + " = #ss")
	public void onSheetCreate(SheetEvent event){
		StringBuilder info = new StringBuilder();
		info.append("Create sheet : ").append(event.getSheetName());
		
		if(isShowEventInfo(event.getName())){
			addInfo(info.toString());
		}
	}
	
	@Listen(Events.ON_SHEET_SELECT + " = #ss")
	public void onSheetSelect(SheetSelectEvent event){
		StringBuilder info = new StringBuilder();
		info.append("Select sheet : ").append(event.getSheetName());
		
		if(isShowEventInfo(event.getName())){
			addInfo(info.toString());
		}
	}
	
	@Listen(Events.ON_AFTER_SHEET_NAME_CHANGE + " = #ss")
	public void onSheetNameChange(SheetEvent event){
		StringBuilder info = new StringBuilder();
		info.append("Rename sheet to ").append(event.getSheetName());
		
		
		if(isShowEventInfo(event.getName())){
			addInfo(info.toString());
		}
	}
	
	@Listen(Events.ON_AFTER_SHEET_ORDER_CHANGE + " = #ss")
	public void onSheetOrderChange(SheetEvent event){
		StringBuilder info = new StringBuilder();
		Sheet sheet = event.getSheet();
		info.append("Reorder sheet : ").append(event.getSheetName())
		.append(" to ").append(sheet.getBook().getSheetIndex(sheet));
		
		if(isShowEventInfo(event.getName())){
			addInfo(info.toString());
		}
	}
	
	
	@Listen(Events.ON_AFTER_SHEET_DELETE + " = #ss")
	public void onSheetDelete(SheetDeleteEvent event){
		StringBuilder info = new StringBuilder();
		info.append("Delete sheet : ").append(event.getSheetName());
		
		if(isShowEventInfo(event.getName())){
			addInfo(info.toString());
		}
	}
	

	
	@Listen(Events.ON_WIDGET_UPDATE + " = #ss")
	public void onWidgetUpdate(WidgetUpdateEvent event){
		StringBuilder info = new StringBuilder();
		SheetAnchor anchor = event.getSheetAnchor();
		
		info.append("Widget ")
				.append(event.getWidgetData())
				.append(" ")
				.append(event.getAction())
				.append(" to ")
				.append(Ranges.getAreaRefString(anchor.getRow(),
						anchor.getColumn(), anchor.getLastRow(),
						anchor.getLastColumn()));
		
		if(isShowEventInfo(event.getName())){
			addInfo(info.toString());
		}
	}
	
	@Listen(Events.ON_WIDGET_CTRL_KEY + " = #ss")
	public void onWidgetCtrlKey(WidgetKeyEvent event){
		StringBuilder info = new StringBuilder();
		
		info.append("Widget ").append(event.getWidgetData())
			.append(" Key : ").append(event.getKeyCode())
			.append(", Ctrl:").append(event.isCtrlKey())
			.append(", Alt:").append(event.isAltKey())
			.append(", Shift:").append(event.isShiftKey());
		
		if(isShowEventInfo(event.getName())){
			addInfo(info.toString());
		}
	}
	
	@Listen(Events.ON_AUX_ACTION + " = #ss")
	public void onAuxAction(AuxActionEvent event){
		StringBuilder info = new StringBuilder();
		
		info.append("AuxAction ").append(event.getAction());
		
		if (!"deleteSheet".equals(event.getAction())){
			//deleted sheet is unable to access at this moment
			info.append(" on : ").append(Ranges.getAreaRefString(event.getSheet(),event.getSelection()));
		}
		
		if(isShowEventInfo(event.getName())){
			addInfo(info.toString());
		}
	}
	
	
	@Listen(Events.ON_CELL_FILTER + " = #ss")
	public void onCellFilter(CellFilterEvent event){
		StringBuilder info = new StringBuilder();

		info.append("Filter button clicked")
			.append(", filter area: ").append(Ranges.getAreaRefString(event.getSheet(), event.getFilterArea()))
			.append(", on field: ").append(event.getField());

		if(isShowEventInfo(event.getName())){
			addInfo(info.toString());
		}
	}

	@Listen(Events.ON_CELL_VALIDATOR + " = #ss")
	public void onCellValidator(CellMouseEvent event){
		StringBuilder info = new StringBuilder();

		info.append("Validation button clicked ")
			.append(" on cell ").append(Ranges.getCellRefString(event.getRow(),event.getColumn()));

		if(isShowEventInfo(event.getName())){
			addInfo(info.toString());
		}
	}

	@Listen(Events.ON_CLIPBOARD_PASTE + " = #ss")
	public void onClipboardPaste(ClipboardPasteEvent event) {
		if(isShowEventInfo(event.getName())){
			StringBuilder info = new StringBuilder();
			info.append("pasted from " + ss.getHighlight());
			info.append(" to " + event.getArea());
			addInfo(info.toString());
		}
	}

	@Listen(Events.ON_CHART_CLICK + " = #ss")
	public void onChartClick(ChartMouseEvent event) {
		if(isShowEventInfo(event.getName())){
			StringBuilder info = new StringBuilder();
			info.append("clicked " + event.getChartName());
			addInfo(info.toString());
		}
	}

	@Listen(Events.ON_CHART_RIGHT_CLICK + " = #ss")
	public void onChartRightClick(ChartMouseEvent event) {
		if(isShowEventInfo(event.getName())){
			StringBuilder info = new StringBuilder();
			info.append("right clicked " + event.getChartName());
			addInfo(info.toString());
		}
	}

	@Listen(Events.ON_CHART_DOUBLE_CLICK + " = #ss")
	public void onChartDoubleClick(ChartMouseEvent event) {
		if(isShowEventInfo(event.getName())){
			StringBuilder info = new StringBuilder();
			info.append("double clicked " + event.getChartName());
			addInfo(info.toString());
		}
	}


	private void initModel() {
		//Available events
		//It is just for showing messages, event is always listened in this demo.
		eventFilterModel.setMultiple(true);
		
		enableEventFilter(Events.ON_START_EDITING,true);
		enableEventFilter(Events.ON_EDITBOX_EDITING,true);
		enableEventFilter(Events.ON_STOP_EDITING,true);
		enableEventFilter(Events.ON_AFTER_CELL_CHANGE,true);
		
		enableEventFilter(Events.ON_CTRL_KEY,true);
		
		enableEventFilter(Events.ON_CELL_CLICK,false);
		enableEventFilter(Events.ON_CELL_DOUBLE_CLICK,true);
		enableEventFilter(Events.ON_CELL_RIGHT_CLICK,true);
		
		enableEventFilter(Events.ON_HEADER_UPDATE,true);
		enableEventFilter(Events.ON_HEADER_CLICK,true);
		enableEventFilter(Events.ON_HEADER_RIGHT_CLICK,true);
		enableEventFilter(Events.ON_HEADER_DOUBLE_CLICK,true);
		
		enableEventFilter(Events.ON_AUX_ACTION,true);
		
		enableEventFilter(Events.ON_CELL_FOCUS,false);
		enableEventFilter(Events.ON_CELL_SELECTION,false);
		enableEventFilter(Events.ON_CELL_SELECTION_UPDATE,true);
		
		enableEventFilter(Events.ON_CELL_FILTER,true);//useless
		enableEventFilter(Events.ON_CELL_VALIDATOR,true);//useless
		
		enableEventFilter(Events.ON_WIDGET_UPDATE,true);
		enableEventFilter(Events.ON_WIDGET_CTRL_KEY,true);
		
		enableEventFilter(Events.ON_AFTER_SHEET_CREATE,true);
		enableEventFilter(Events.ON_AFTER_SHEET_DELETE,true);
		enableEventFilter(Events.ON_AFTER_SHEET_NAME_CHANGE,true);
		enableEventFilter(Events.ON_AFTER_SHEET_ORDER_CHANGE,true);
		enableEventFilter(Events.ON_SHEET_SELECT,true);
		
		enableEventFilter(Events.ON_CELL_HYPERLINK,true);
		enableEventFilter(Events.ON_CLIPBOARD_PASTE,false);
		//chart clicking
		enableEventFilter(Events.ON_CHART_CLICK,false);
		enableEventFilter(Events.ON_CHART_RIGHT_CLICK,false);
		enableEventFilter(Events.ON_CHART_DOUBLE_CLICK,false);

		eventFilterList.setModel(eventFilterModel);

		//add default show only
		infoList.setModel(infoModel);
		addInfo("Spreadsheet initialized");		
		
		//hint for special cells
		//D1
		Ranges.range(ss.getSelectedSheet(), 0, 3).setCellEditText("Edit Me");
		//E1
		Ranges.range(ss.getSelectedSheet(), 0, 4).setCellEditText("Edit Me");
		//D3
		Ranges.range(ss.getSelectedSheet(), 2, 3).setCellEditText("Edit Me");
		//E3
		Ranges.range(ss.getSelectedSheet(), 2, 4).setCellEditText("Edit Me");
	}
	
	private void enableEventFilter(String event, boolean showInfo){
		if(!eventFilterModel.contains(event)){
			eventFilterModel.add(event);
		}
		if(showInfo){
			eventFilterModel.addToSelection(event);
		}else{
			eventFilterModel.removeFromSelection(event);
		}
	}
	
	private boolean isShowEventInfo(String event){
		return eventFilterModel.getSelection().contains(event);
	}

	@Listen("onClick = #clearAllFilter")
	public void onClearAllFilter(){
		eventFilterModel.clearSelection();
	}
	@Listen("onClick = #selectAllFilter")
	public void onSelectAll(){
		eventFilterModel.clearSelection();
		eventFilterModel.setSelection(new ArrayList<String>(eventFilterModel));
	}
	
	private void addInfo(String info){
		infoModel.add(0, info);
		while(infoModel.size()>100){
			infoModel.remove(infoModel.size()-1);
		}
	}

	@Listen("onClick = #clearInfo")
	public void onClearInfo(){
		infoModel.clear();
	}


	/**
	 * when a cell has 0, set its text color as the background color to make it looks like a blank cell.
	 */
	private void makeTextAsBlank(StopEditingEvent event) {
		Range range = RangeHelper.getTargetRange(event);
		if (event.getEditingValue().toString().equals("0")){
			Color backgroundColor = range.getCellStyle().getBackColor();
			CellOperationUtil.applyFontColor(range, backgroundColor.getHtmlColor());
		}else {
			//reset to default text color for a non-0 cell
			if (range.getCellValue() != null && range.getCellData().getDoubleValue().intValue()==0) {
				CellOperationUtil.applyFontColor(range, ColorImpl.BLACK.getHtmlColor());
			}
		}
	}

	/**
	 * ON_CELL_FILTER //useless
	 * ON_CELL_VALIDATOR //useless
	 */
}



