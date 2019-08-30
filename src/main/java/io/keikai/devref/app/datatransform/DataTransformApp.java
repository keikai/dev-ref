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
	
	private static final String TRANSFORM_BOOK_NAME = "transform.xlsx";
	
	private static final String BOOK_FOLDER = Configuration.DEFAULT_BOOK_FOLDER + "/data-transform";
	
	/* Initialization code for the client engine */
	@Override
	public void init(String keikaiEngineAddress) {
		/* Open a client connection to the server located at keikaiEngineAddress */
		mainSpreadsheet = Keikai.newClient(keikaiEngineAddress);
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
		mainSpreadsheet.getUIService().showToolbar(false);
		mainSpreadsheet.getUIService().showSheetTabs(false);
		mainSpreadsheet.getUIService().showContextMenu(true);
		mainSpreadsheet.getUIService().showFormulaBar(false);
		mainSpreadsheet.getUIService().showSheetControls(false);
		mainSpreadsheet.getUIService().setProtectedSheetWarningEnabled(false);
	}

	@Override
	public String getJavaScriptURI(String domId) {
		return mainSpreadsheet.getURI(domId);
	}

	@Override
	public void run() {
		try {
			/* Import the spreadsheet from file for each new client */
			mainSpreadsheet.importAndReplace(TRANSFORM_BOOK_NAME, new File(BOOK_FOLDER, TRANSFORM_BOOK_NAME));
			/* set payrollSheet as the active (visible) sheet in client */
			mainSpreadsheet.setActiveWorkbook(TRANSFORM_BOOK_NAME);
			mainSpreadsheet.getWorkbook(TRANSFORM_BOOK_NAME).setActiveWorksheet("Source");
			Worksheet sourceSheet = mainSpreadsheet.getWorkbook(TRANSFORM_BOOK_NAME).getWorksheet();
			/* register event listener on the button named "confirmButton" */
			sourceSheet.getButton("toDisplay1").addAction(buttonShapeMouseEvent -> {
				goToSheet("Display1");
			});
			sourceSheet.getButton("toDisplay2").addAction(buttonShapeMouseEvent -> {
				goToSheet("Display2");
			});
		} catch (FileNotFoundException | AbortedException e) {
			e.printStackTrace();
		}
	}

	private void goToSheet(String target) {
		mainSpreadsheet.getWorkbook().getWorksheet(target).activate();
	}

}
