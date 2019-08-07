package io.keikai.devref.app.datatransform;

import io.keikai.client.api.*;
import io.keikai.client.api.event.*;
import io.keikai.client.api.ui.UIActivity;
import io.keikai.devref.*;
import io.keikai.util.DateUtil;

import java.io.*;
import java.util.*;

public class DataTransformApp implements UseCase {
	
	private Spreadsheet mainSpreadsheet;
	private Spreadsheet hiddenspreadsheet;
	
	private static final String SOURCE_BOOK_NAME = "source.xlsx";
	private static final String DISPLAY_BOOK_NAME = "display.xlsx";
	private static final String TRANSFORM_BOOK_NAME = "transform.xlsx";
	
	private static final String BOOK_FOLDER = Configuration.DEFAULT_BOOK_FOLDER + "/data-transform";
	
	private static final String[] INPUT_FIELD_NAMES;
	private static final String[] OUTPUT_FIELD_NAMES;
	
	static {
		INPUT_FIELD_NAMES = new String[] {"data1","data2","data3","data4","data5"};
		OUTPUT_FIELD_NAMES = new String[] {"output1","output2","output3","output4","output5","output6"};
	}

	/* Initialization code for the client engine */
	@Override
	public void init(String keikaiEngineAddress) {
		/* Open a client connection to the server located at keikaiEngineAddress */
		mainSpreadsheet = Keikai.newClient(keikaiEngineAddress);
		hiddenspreadsheet = Keikai.newClient(keikaiEngineAddress);
		/* Sets up callbacks for the browser. */
		mainSpreadsheet.setUIActivityCallback(new UIActivity() {

			/* initialize the UI upon load */
			@Override
			public void onConnect() {
				setupUi();
			}

			/* terminates the client on browser closed */
			@Override
			public void onDisconnect() {
				mainSpreadsheet.close();
			}
		});
	}

	private void setupUi() {
		mainSpreadsheet.getUIService().showToolbar(true);
		mainSpreadsheet.getUIService().showSheetTabs(true);
		mainSpreadsheet.getUIService().showContextMenu(true);
		mainSpreadsheet.getUIService().showFormulaBar(true);
		mainSpreadsheet.getUIService().showSheetControls(true);
		mainSpreadsheet.getUIService().setProtectedSheetWarningEnabled(true);
	}

	@Override
	public String getJavaScriptURI(String domId) {
		return mainSpreadsheet.getURI(domId);
	}

	@Override
	public void run() {
		try {
			/* Import the spreadsheet from file for each new client */
			mainSpreadsheet.importAndReplace(SOURCE_BOOK_NAME, new File(BOOK_FOLDER, SOURCE_BOOK_NAME));
			/* set payrollSheet as the active (visible) sheet in client */
			mainSpreadsheet.setActiveWorkbook(SOURCE_BOOK_NAME);
			mainSpreadsheet.getWorkbook(SOURCE_BOOK_NAME).setActiveWorksheet("Sheet1");
			Worksheet sourceSheet = mainSpreadsheet.getWorkbook(SOURCE_BOOK_NAME).getWorksheet();
			/* register event listener on the button named "confirmButton" */
			sourceSheet.getButton("commitButton").addAction(buttonShapeMouseEvent -> {
				doDataProcess();
			});
		} catch (FileNotFoundException | AbortedException e) {
			e.printStackTrace();
		}
	}

	private void doDataProcess() throws FileNotFoundException, AbortedException {
		Map<String,Object> inputData = retrieveDataFromSourceSheet();
		pushDataToTransformSheet(inputData);
		Map<String,Object> outputData = retrieveDataFromTransformSheet();
		pushDataToDisplaySheet(outputData);
		mainSpreadsheet.setActiveWorkbook(DISPLAY_BOOK_NAME);
		mainSpreadsheet.getWorkbook(DISPLAY_BOOK_NAME).setActiveWorksheet(0);
	}
	private Map<String, Object> retrieveDataFromSourceSheet() {
		return retrieveData(mainSpreadsheet, INPUT_FIELD_NAMES);
	}
		
	private Map<String, Object> retrieveDataFromTransformSheet() {
		Map<String, Object> resultData = retrieveData(hiddenspreadsheet, OUTPUT_FIELD_NAMES);
		hiddenspreadsheet.close();
		return resultData;
	}

	private Map<String, Object> retrieveData(Spreadsheet targetSpreadSheet, String[] fieldList) {
		Map<String, Object> result = new HashMap<String, Object>();
		for (String field : fieldList) {
			result.put(field, targetSpreadSheet.getRangeByName(field).getValue());
		}
		return result;
	}
	
	private void pushDataToTransformSheet(Map<String, Object> inputData) throws FileNotFoundException, AbortedException {
		hiddenspreadsheet.importAndReplace(TRANSFORM_BOOK_NAME, new File(BOOK_FOLDER, TRANSFORM_BOOK_NAME));
		hiddenspreadsheet.setActiveWorkbook(TRANSFORM_BOOK_NAME);
		hiddenspreadsheet.getWorkbook(TRANSFORM_BOOK_NAME).setActiveWorksheet("Sheet1");
		pushData(hiddenspreadsheet, inputData, INPUT_FIELD_NAMES);
	}

	private void pushDataToDisplaySheet(Map<String, Object> outputData) throws FileNotFoundException, AbortedException {
		mainSpreadsheet.importAndReplace(DISPLAY_BOOK_NAME, new File(BOOK_FOLDER, DISPLAY_BOOK_NAME));
		mainSpreadsheet.setActiveWorkbook(DISPLAY_BOOK_NAME);
		mainSpreadsheet.getWorkbook(DISPLAY_BOOK_NAME).setActiveWorksheet("Sheet1");
		pushData(mainSpreadsheet, outputData, OUTPUT_FIELD_NAMES);
	}

	private void pushData(Spreadsheet targetSpreadSheet, Map<String, Object> data, String[] fieldList) {
		for (String field : fieldList) {
			targetSpreadSheet.getRangeByName(field).setValue(data.get(field));
		}
	}



}
