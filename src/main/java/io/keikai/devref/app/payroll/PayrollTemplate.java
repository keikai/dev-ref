package io.keikai.devref.app.payroll;

import io.keikai.client.api.*;
import io.keikai.client.api.event.*;
import io.keikai.client.api.ui.UIActivity;
import io.keikai.devref.*;
import io.keikai.util.DateUtil;

import java.io.*;
import java.util.*;

public class PayrollTemplate implements UseCase {
	/* Payroll data table sheet - represents one tab in excel */
	private Worksheet payrollSheet;
	private static final String PAYROLL_SUMMARY_SHEET_NAME = "Payroll";
	/* Payroll template sheet - represents one tab in excel */
	private Worksheet slipTemplateSheet;
	private static final String PAYROLL_SLIP_FORM_SHEET_NAME = "Form";
	/* Payroll spreadsheet - represents one excel file */
	private Spreadsheet spreadsheet;
	private static final String BOOK_NAME = "payroll.xlsx";

	/* Initialization code for the client engine */
	@Override
	public void init(String keikaiEngineAddress) {
		/* Open a client connection to the server located at keikaiEngineAddress */
		spreadsheet = Keikai.newClient(keikaiEngineAddress);
		/* Sets up callbacks for the browser. */
		spreadsheet.setUIActivityCallback(new UIActivity() {

			/* initialize the UI upon load */
			@Override
			public void onConnect() {
				setupUi();
			}

			/* terminates the client on browser closed */
			@Override
			public void onDisconnect() {
				spreadsheet.close();
			}
		});
	}

	private void setupUi() {
		spreadsheet.getUIService().showToolbar(true);
		spreadsheet.getUIService().showSheetTabs(true);
		spreadsheet.getUIService().showContextMenu(true);
		spreadsheet.getUIService().showFormulaBar(true);
		spreadsheet.getUIService().showSheetControls(true);
		spreadsheet.getUIService().setProtectedSheetWarningEnabled(true);
	}

	@Override
	public String getJavaScriptURI(String domId) {
		return spreadsheet.getURI(domId);
	}

	@Override
	public void run() {
		try {
			/* Import the spreadsheet from file for each new client */
			spreadsheet.importAndReplace(BOOK_NAME, new File(Configuration.DEFAULT_BOOK_FOLDER, BOOK_NAME));
			/* locate both worksheet by name */
			payrollSheet = spreadsheet.getWorksheet(PAYROLL_SUMMARY_SHEET_NAME);
			slipTemplateSheet = spreadsheet.getWorksheet(PAYROLL_SLIP_FORM_SHEET_NAME);
			/* set payrollSheet as the active (visible) sheet in client */
			payrollSheet.activate();
			/* register event listener on the button named "confirmButton" */
			payrollSheet.getButton("ConfirmButton").addAction(buttonShapeMouseEvent -> {
				List<Map<String, Object>> dataset = retrieveAllData(PAYROLL_SUMMARY_SHEET_NAME,"PayrollTable");
				generateAllTemplates(dataset);
			});
		} catch (FileNotFoundException | AbortedException e) {
			e.printStackTrace();
		}
	}

	/* retrieve tabular data from range "rangeName" in sheet "sheetName" */
	private List<Map<String, Object>> retrieveAllData(String sheetName, String rangeName) {
		Range payrollRange = spreadsheet.getRangeByName(sheetName, rangeName);
		/*retrieve the first line of the range, assumed to be headers*/
		Range header = payrollRange.getRows(0);
		/* lastcolumn + 1 because index starts from 0 and columns numbers starts from 1*/
		int columnCount = header.getLastColumn() + 1;
		/* store column headers in a list*/
		List<String> headersList = new ArrayList<String>();
		for (int i = 0; i < columnCount; i++) {
			headersList.add(header.getCell(0, i).getValue());
		}
		/* get Columns*/
		Range firstColumn = payrollRange.getColumns(0);
		/* not adding 1 due to index/number conversion because the first row is headers, therefore there is one less row to count*/
		int rowCount = firstColumn.getLastRow();
		/* stores tabular value with structure [{prop1:row1value1, prop2:row1value2},{prop1:row2value1, prop2:row2value2},...]*/
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			Map<String, Object> resultRow = new HashMap<String, Object>();
			for (int colIndex = 0; colIndex < columnCount; colIndex++) {
				resultRow.put(headersList.get(colIndex), payrollRange.getCell(rowIndex + 1, colIndex).getValue());
			}
			result.add(resultRow);
		}
		return result;
	}

	/* create a sheet for each entry in the tabular data */
	/* for each key/value pair, search the sheet for a cell holding the same name as the key (ex : Name) and fills the value in this cell if found*/
	private void generateAllTemplates(List<Map<String, Object>> dataset) {
		for (Map<String, Object> rowData : dataset) {
			/* clone the template sheet to the end of the sheet list*/
			Worksheet cloneTemplate = slipTemplateSheet.copyToEnd(slipTemplateSheet.getWorkbook());
			/* rename the sheet if a name is available*/
			String sheetName = (String) rowData.get("Name");
			if(sheetName!=null && !"".equals(sheetName)) {
				cloneTemplate.rename(sheetName);
			}
			/*write every key/value pair to the corresponding named cell in the target template*/
			for (Map.Entry<String, Object> entry : rowData.entrySet()) {
				writeEntryToNamedFields(cloneTemplate, entry.getKey(), entry.getValue());
			}
		}
	}

	private void writeEntryToNamedFields(Worksheet targetSheet, String key, Object value) {
		/*find cell coordinates with the same name as the key in the template sheet and fill the value in the cloned sheet*/
		Range rangeByName = spreadsheet.getRangeByName(PAYROLL_SLIP_FORM_SHEET_NAME,key);
		if (rangeByName != null) {
			targetSheet.getRange(rangeByName.getA1Notation()).setValue(value);
		}
	}
}
