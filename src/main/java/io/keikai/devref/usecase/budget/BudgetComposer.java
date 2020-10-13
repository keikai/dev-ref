package io.keikai.devref.usecase.budget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;

import io.keikai.api.Range;
import io.keikai.api.Ranges;
import io.keikai.ui.Spreadsheet;
import io.keikai.ui.event.SheetSelectEvent;

public class BudgetComposer extends SelectorComposer<Component>{

	private static final String SUMMARY_SHEET_NAME = "Summary";
	 // key:value = sheet name : table named Range
	private HashMap<String,String> sheetNames;
	private List<String> columnOrder;
	
	@Wire("spreadsheet")
    private Spreadsheet spreadsheet;


    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        spreadsheet.setShowSheetbar(true);
        spreadsheet.setShowSheetTabContextMenu(true);
        spreadsheet.setShowToolbar(true);
        spreadsheet.setShowFormulabar(true);
        spreadsheet.setShowSheetbar(true);
        sheetNames = new HashMap<String, String>();
        sheetNames.put("Expense-RD", "RDsrc");
        sheetNames.put("Expense-MKT", "MKTsrc");
        sheetNames.put("Expense-OP", "OPsrc");
        sheetNames.put("Expense-Adm", "ADMsrc");
		columnOrder = new ArrayList<String>(Arrays.asList("Expense-RD", "Expense-MKT", "Expense-OP", "Expense-Adm"));
		spreadsheet.getBook().getSheet(SUMMARY_SHEET_NAME).getCharts();
		
    }
    



	@Listen(io.keikai.ui.event.Events.ON_SHEET_SELECT + "=spreadsheet")
    public void onSheetSelect(SheetSelectEvent e) {
        String sheetName = e.getSheet().getSheetName();
        if(SUMMARY_SHEET_NAME.equals(sheetName)) {
        	doMergeWorkflow();
        }
    }

	private void doMergeWorkflow() {
		List<BudgetEntry> budgetEntries = getBudgetData();
		Range periodTable = Ranges.rangeByName(spreadsheet.getBook().getSheet(SUMMARY_SHEET_NAME), "budgetPerPeriod");
		fillPeriodTable(periodTable, mergePeriodData(budgetEntries));
		Range deptTable = Ranges.rangeByName(spreadsheet.getBook().getSheet(SUMMARY_SHEET_NAME), "budgetPerDept");
		fillDeptTable(deptTable, mergeDeptData(budgetEntries));
		
	}
	
	private void fillPeriodTable(Range periodTable, Map<String, List<Number>> mergedMap) {
		for (int row = periodTable.getRow(); row < periodTable.getRow() + periodTable.getRowCount(); row++) {
			String rowLabel = (String) Ranges.range(periodTable.getSheet(), row, 0).getCellValue();
			for (int col = 0; col < 4; col++) {
				Ranges.range(periodTable.getSheet(), row, col+1).setCellValue(mergedMap.get(rowLabel).get(col));
			}
		}
	}


	private void fillDeptTable(Range deptTable, Map<String, List<Number>> mergedMap) {
		for (int row = deptTable.getRow(); row < deptTable.getRow() + deptTable.getRowCount(); row++) {
			String rowLabel = (String) Ranges.range(deptTable.getSheet(), row, 0).getCellValue();
			for (int col = 0; col < 4; col++) {
				Ranges.range(deptTable.getSheet(), row, col+1).setCellValue(mergedMap.get(rowLabel).get(col));
			}
		}
	}

	private List<BudgetEntry> getBudgetData(){
		List<BudgetEntry> sheetEntries = new ArrayList<BudgetEntry>();
		for (Map.Entry<String, String> sheetNamesEntry : sheetNames.entrySet()) {
			sheetEntries.addAll(getDeptBudgetEntries(Ranges.rangeByName(spreadsheet.getBook().getSheet(sheetNamesEntry.getKey()), sheetNamesEntry.getValue()),sheetNamesEntry.getKey()));
		}
		return sheetEntries;
	}

	private Collection<? extends BudgetEntry> getDeptBudgetEntries(Range dataRange, String deptName) {
		List<BudgetEntry> entries = new ArrayList<BudgetEntry>();
		for (int row = dataRange.getRow(); row < dataRange.getRow() + dataRange.getRowCount(); row++) {
			List<Number> periodList = new ArrayList<Number>();
			for (int col = 1; col <= 4; col++) {
				periodList.add((Number)Ranges.range(dataRange.getSheet(), row, col).getCellValue());
			}
			entries.add(new BudgetEntry((String)Ranges.range(dataRange.getSheet(), row, 0).getCellValue(),deptName, periodList));
		}
		return entries;
	}

	private Map<String,List<Number>> mergePeriodData(List<BudgetEntry> dataList){
		Map<String,List<Number>> result = new HashMap<String, List<Number>>();
		for (BudgetEntry budgetEntry : dataList) {
			if(!result.containsKey(budgetEntry.getLabel())) {
				result.put(budgetEntry.getLabel(), new ArrayList<Number>(Arrays.asList(0,0,0,0)));
			}
			List<Number> newData = new ArrayList<Number>();
			for (int i = 0; i < 4; i++) {
				result.get(budgetEntry.getLabel()).set(i,budgetEntry.getPeriods().get(i).doubleValue() +result.get(budgetEntry.getLabel()).get(i).doubleValue());
			}
		}
		return result;
	}

	private Map<String,List<Number>> mergeDeptData(List<BudgetEntry> dataList){
		Map<String,List<Number>> result = new HashMap<String, List<Number>>();
		for (BudgetEntry budgetEntry : dataList) {
			if(!result.containsKey(budgetEntry.getLabel())) {
				result.put(budgetEntry.getLabel(), new ArrayList<Number>(Arrays.asList(0,0,0,0)));
			}
			int position = columnOrder.indexOf(budgetEntry.getDept());
			double value = 0;
			for (int i = 0; i < 4; i++) {
				value += budgetEntry.getPeriods().get(i).doubleValue() ;
			}
			result.get(budgetEntry.getLabel()).set(position,value + result.get(budgetEntry.getLabel()).get(position).doubleValue());
		}
		return result;
	}
    
}