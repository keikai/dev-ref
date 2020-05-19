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
    final private static String EMPLOYEE_SHEET = "Payroll";
	private Range generateButton;
	private Sheet sheet;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		sheet = spreadsheet.getBook().getSheet(EMPLOYEE_SHEET);
		generateButton = Ranges.rangeByName(sheet, "Generate");
	}

	@Listen(Events.ON_CELL_CLICK + "=spreadsheet")
    public void onCellClick(CellMouseEvent e) {
        String sheetName = e.getSheet().getSheetName();
        switch (sheetName) {
            case EMPLOYEE_SHEET:
            	if (RangeHelper.isRangeClicked(e, generateButton))
            		fillPayrollSlips();
                break;
        }
    }

    private void fillPayrollSlips() {
		String tableName = "PayrollTable";
		Range payrollRange = Ranges.rangeByName(sheet, tableName);
    	List<Map<String, Object>> employeeSalaries = getEmployeeSalaries(payrollRange);
    	generateAllPayrollSlips(employeeSalaries);
	}

	private void generateAllPayrollSlips(List<Map<String, Object>> employeeSalaries) {
		for (Map<String, Object> employee : employeeSalaries) {
			Sheet payrollSheet = Ranges.range(spreadsheet.getBook().getSheet("Form"))
					.cloneSheet((String) employee.get("Name"));
			for (String field : employee.keySet()) {
				Ranges.rangeByName(payrollSheet, field).setCellValue(employee.get(field));
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

	private List<Map<String, Object>> getEmployeeSalaries(Range table) {
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