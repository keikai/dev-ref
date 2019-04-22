package io.keikai.devref.app;

import io.keikai.client.api.*;
import io.keikai.client.api.ui.UIActivity;
import io.keikai.devref.UseCase;

import java.io.*;
import java.util.*;

import static io.keikai.devref.Configuration.DEFAULT_BOOK_FOLDER;

/**
 * This application allows users to generate their own reports by selecting one or multiple report templates.
 */
public class ReportBuilder implements UseCase {
    private Spreadsheet spreadsheet;
    private String defaultXlsx = "Travel Management.xlsx"; //main UI to select report templates
    private File REPORT_FOLDER = new File(DEFAULT_BOOK_FOLDER, "report");

    //assume each checkbox' name matches template file name
    private String[] templateNames = {"Travel Reservation Form", "Travel Insurance Form", "Travel Health Management", "Currency Exchange Request"};
    // template name : imported Workbook
    private Map<String, Workbook> templateMap = new HashMap();

    private static final SheetProtection VIEW_ONLY = new SheetProtection.Builder().setPassword("").build();
    private Worksheet mainSheet; // main UI to pick report templates
    private Workbook reportBook; // the book to store the final report
    private boolean atLeast1TemplateSelected = false;

    @Override
    public void init(String keikaiEngineAddress) {
        spreadsheet = Keikai.newClient(keikaiEngineAddress);
        //TODO make it a helper class
        spreadsheet.setUIActivityCallback(new UIActivity() {
            /**
             * We need to call UIService in a UIActivity callback since a UI client has connected now.
             */
            @Override
            public void onConnect() {
                toAppMode();
            }

            @Override
            public void onDisconnect() {
                spreadsheet.close();
            }
        });
    }

    private void toAppMode() {
        spreadsheet.getUIService().showToolbar(false);
        spreadsheet.getUIService().showSheetTabs(false);
        spreadsheet.getUIService().showContextMenu(false);
        spreadsheet.getUIService().setProtectedSheetWarningEnabled(false);
    }

    private void toEditMode() {
        spreadsheet.getUIService().showToolbar(true);
        spreadsheet.getUIService().showSheetTabs(true);
        spreadsheet.getUIService().showContextMenu(true);
        spreadsheet.getUIService().setProtectedSheetWarningEnabled(true);
    }


    @Override
    public String getJavaScriptURI(String domId) {
        return spreadsheet.getURI(domId);
    }

    @Override
    public void run() {
        try {
            reportBook = spreadsheet.getWorkbook();
            importTemplates();
            Workbook mainBook = spreadsheet.imports(defaultXlsx, new File(REPORT_FOLDER, defaultXlsx));
            mainSheet = mainBook.getWorksheet();
            mainSheet.protect(VIEW_ONLY);
            mainSheet.setVisibleArea("A1:F14");
            spreadsheet.setActiveWorkbook(mainBook.getName());
            addEventListener();
            spreadsheet.getWorksheet().isAutoFilterEnabled();
        } catch (FileNotFoundException | AbortedException | DuplicateNameException e) {
            e.printStackTrace();
        }
    }

    private void importTemplates() throws FileNotFoundException, AbortedException, DuplicateNameException {
        for (String name : templateNames) {
            Workbook templateBook = spreadsheet.imports(name, new File(REPORT_FOLDER, name + ".xlsx"));
            templateMap.put(name, templateBook);
        }
    }

    private void addEventListener() {
        spreadsheet.getWorksheet().getButton("build-book").addAction(buttonShapeMouseEvent -> {
            collectSelectedTemplates();
            if (atLeast1TemplateSelected) {
                toEditMode();
                reportBook.deleteWorksheet(0);
                spreadsheet.setActiveWorkbook(reportBook.getName());
            }
        });
    }


    private void collectSelectedTemplates() {
        atLeast1TemplateSelected = false;
        for (String name : templateNames) {
            if (mainSheet.getCheckbox(name).isChecked()) {
                templateMap.get(name).getWorksheet().copyToEnd(reportBook);
                atLeast1TemplateSelected = true;
            }
        }
    }
}
