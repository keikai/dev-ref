package io.keikai.devref.app.exchange;

import io.keikai.client.api.*;
import io.keikai.client.api.event.*;
import io.keikai.client.api.ui.UIActivity;
import io.keikai.devref.*;
import io.keikai.util.DateUtil;

import java.io.*;
import java.util.*;

public class CurrencyExchange implements KeikaiCase {
    private Spreadsheet spreadsheet;
    private Worksheet selectSheet;
    private Worksheet exchangeSheet;
    private Worksheet listSheet;
    private Map<String, Double> rates;
    private String destinationCurrency;
    private Double destinationRate;

    private static final String BOOK_NAME = "currencyExchange.xlsx";
    private static final SheetProtection PROTECTION = new SheetProtection.Builder().setPassword("").setAllowSelectLockedCells(true).setAllowFiltering(true).build();
    private static final String COST_CELL = "cost"; // range name

    @Override
    public void init(String keikaiEngineAddress) {
        spreadsheet = Keikai.newClient(keikaiEngineAddress);
        spreadsheet.setUIActivityCallback(new UIActivity() {
            @Override
            public void onConnect() {
                setupAccessibility();
            }

            @Override
            public void onDisconnect() {
                spreadsheet.close();
            }
        });
    }


    @Override
    public String getJavaScriptURI(String domId) {
        return spreadsheet.getURI(domId);
    }


    @Override
    public void run() {
        try {
            spreadsheet.importAndReplace(BOOK_NAME, new File(Configuration.DEFAULT_BOOK_FOLDER, BOOK_NAME));
            selectSheet = spreadsheet.getWorksheet("select");
            exchangeSheet = spreadsheet.getWorksheet("exchange");
            listSheet = spreadsheet.getWorksheet("list");
            selectSheet.activate();
            addEventListener();
            displayExchangeRate();
            unlockCostInput();
            setupVisibleArea();
        } catch (FileNotFoundException | AbortedException e) {
            e.printStackTrace();
        }
    }

    private void setupVisibleArea() {
        selectSheet.setVisibleArea("A1:O17");
        exchangeSheet.setVisibleArea("A1:L12");
        listSheet.setVisibleArea("A:M");
    }

    /**
     * unlock cost cell that a user will input
     */
    private void unlockCostInput() {
        Range costCell = spreadsheet.getRangeByName(exchangeSheet.getSheetId(), "cost");
        CellStyle unlockedStyle = costCell.createCellStyle();
        Protection protection = unlockedStyle.createProtection();
        protection.setLocked(false);
        unlockedStyle.setProtection(protection);
        costCell.setCellStyle(unlockedStyle);
        exchangeSheet.protect(PROTECTION);
    }

    /**
     * To ensure calling UIService after a UI client is connected. We call them in a UIActivity callback.
     */
    private void setupAccessibility() {
        spreadsheet.getUIService().showToolbar(false);
        spreadsheet.getUIService().showSheetTabs(false);
        spreadsheet.getUIService().showContextMenu(false);
        spreadsheet.getUIService().setProtectedSheetWarningEnabled(false);
    }

    private void displayExchangeRate() {
        String[] INTERESTED_CURRENCY_LIST = {"USD", "GBP" , "AUD", "CHF", "NZD", "JPY", "CAD"};
        int startingRow = 8;
        try {
            rates = ExchangeRateFetcher.fetch();
            for (String currency : INTERESTED_CURRENCY_LIST){
                spreadsheet.getRange(startingRow, 6, 1, 2).setValues(currency, rates.get(currency));
                startingRow++;
            }
        } catch (IOException e) {
            spreadsheet.getRange(startingRow, 0).setValue(e.getMessage());
        }
    }

    private void addEventListener() {
        //listen to selecting destination currency
        spreadsheet.addEventListener(Events.ON_CELL_CLICK, (CellMouseEvent event) -> {
            Range range = event.getRange();
            if (range.getWorksheet().getSheetId().equals(selectSheet.getSheetId())) {
                destinationCurrency = spreadsheet.getRange(range.getRow(), 6).getValue().toString();
                if (rates.containsKey(destinationCurrency)) {
                    destinationRate = rates.get(destinationCurrency);
                    fillDestinationCurrencyRate();

                    exchangeSheet.activate();
                }

            }
        });

        //listen to buy button
        exchangeSheet.getButton("Buy").addAction(buttonShapeMouseEvent -> {
            placeAnOrder();
            listSheet.activate();
        });

        //listen to "buy another" button
        listSheet.getButton("BuyAnother").addAction(buttonShapeMouseEvent -> {
            selectSheet.activate();
        });
    }


    /**
     * place an order in the transaction table
     */
    private void placeAnOrder() {
        listSheet.unprotect("");
        Range costCell = spreadsheet.getRangeByName(exchangeSheet.getSheetId(), COST_CELL);
        if (!costCell.toString().isEmpty()) {
            Double cost = costCell.getRangeValue().getCellValue().getDoubleValue();
            if (cost > 0) {
                Double amount = spreadsheet.getRangeByName(exchangeSheet.getSheetId(), "amount").getRangeValue().getCellValue().getDoubleValue();
                Range orderTable1stRow = spreadsheet.getRange(BOOK_NAME, listSheet.getSheetId(), "C6:G6");
                orderTable1stRow.getEntireRow().insert(Range.InsertShiftDirection.ShiftDown, Range.InsertFormatOrigin.RightOrBelow);
                orderTable1stRow.setValues(DateUtil.getExcelDate(new Date()), cost, destinationCurrency, destinationRate, amount);
            }
        }
        listSheet.protect(PROTECTION);
    }

    /**
     * fill user-selected currency and rate into "exchange" sheet
     */
    private void fillDestinationCurrencyRate() {
        exchangeSheet.unprotect("");
        spreadsheet.getRangeByName(exchangeSheet.getName(), "rate").setValue(destinationRate);
        spreadsheet.getRange(BOOK_NAME, exchangeSheet.getName(), "H9").setValue(destinationCurrency);
        spreadsheet.getRangeByName(exchangeSheet.getName(), COST_CELL).activate();
        exchangeSheet.protect(PROTECTION);
    }
}
