package io.keikai.devref.usecase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.keikai.devref.util.RangeHelper;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;

import io.keikai.api.Range;
import io.keikai.api.Ranges;
import io.keikai.api.model.Sheet;
import io.keikai.ui.Spreadsheet;
import io.keikai.ui.event.CellMouseEvent;
import io.keikai.ui.event.Events;

public class PayrollComposer extends SelectorComposer<Component>{

    @Wire("spreadsheet")
    private Spreadsheet spreadsheet;
    final private static String SELECT_SHEET = "Payroll";
	private Range generateButton;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		generateButton = Ranges.rangeByName(spreadsheet.getBook().getSheet(SELECT_SHEET), "Generate");
	}

	@Listen(Events.ON_CELL_CLICK + "=spreadsheet")
    public void onCellClick(CellMouseEvent e) {
        String sheetName = e.getSheet().getSheetName();
        switch (sheetName) {
            case SELECT_SHEET:
            	if (RangeHelper.isRangeClicked(e, generateButton))
            		fillForm();
                break;
        }
    }

    private void fillForm() {
    	Sheet sheet = spreadsheet.getBook().getSheet("Payroll");
		String tableName = "PayrollTable";
		Range payrollRange = Ranges.rangeByName(sheet, tableName);
    	List<Map<String, Object>> allRows = getAllRows(payrollRange);
    	generateAllTemplates(allRows);
	}

	private void generateAllTemplates(List<Map<String, Object>> allRows) {
		for (Map<String, Object> row : allRows) {
			Sheet cloneSheet = Ranges.range(spreadsheet.getBook().getSheet("Form")).cloneSheet((String) row.get("Name"));
			for (String head : row.keySet()) {
				Ranges.rangeByName(cloneSheet, head).setCellValue(row.get(head));
			}
		}
	}


	private List<String> getAllHeaders(Range table) {
		List<String> headers = new ArrayList<String>();
		for (int i = table.getColumn(); i < table.getColumnCount(); i++) {
			headers.add(table.toCellRange(-1, i).getCellValue().toString());
		}
		return headers;
	}

	private List<Map<String, Object>> getAllRows(Range table) {
		List<Map<String, Object>> allRows = new ArrayList<Map<String,Object>>();
		List<String> headers = getAllHeaders(table);
		for (int j = table.getRow() - 1; j < table.getRowCount(); j++) {
			Map<String, Object> row = new HashMap<String, Object>();
			for (int i = table.getColumn(); i < table.getColumnCount(); i++) {
				row.put(headers.get(i), table.toCellRange(j, i).getCellValue());
			}
			allRows.add(row);
		}
		return allRows;
	}
    
    
}