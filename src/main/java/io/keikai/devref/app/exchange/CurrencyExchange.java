package io.keikai.devref.app.exchange;

import io.keikai.client.api.*;
import io.keikai.client.api.event.*;
import io.keikai.devref.*;

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

    public static final String BOOK_NAME = "currencyExchange.xlsx";
    public static final SheetProtection PROTECTION = new SheetProtection.Builder().setPassword("").setAllowSelectLockedCells(true).setAllowFiltering(true).build();

    @Override
    public void init(String keikaiEngineAddress) {
        spreadsheet = Keikai.newClient(keikaiEngineAddress);
    }


    @Override
    public String getJavaScriptURI(String domId) {
        return spreadsheet.getURI(domId);
    }


    @Override
    public void run() {
        try {
            spreadsheet.importAndReplace(BOOK_NAME, new File(Configuration.DEFAULT_BOOK_FOLDER, BOOK_NAME));
            configureUI();
            selectSheet = spreadsheet.getWorksheet("select");
            exchangeSheet = spreadsheet.getWorksheet("exchange");
            listSheet = spreadsheet.getWorksheet("list");
            selectSheet.activate();
            addEventListener();
            displayExchangeRate();
        } catch (FileNotFoundException | AbortedException e) {
            e.printStackTrace();
//            throw new IOException(e);
        }
    }

    private void configureUI() {
        spreadsheet.getUIService().showToolbar(false);
        spreadsheet.getUIService().showContextMenu(false);
        spreadsheet.getUIService().showSheetTabs(false);
    }
    private void displayExchangeRate() {
        int startingRow = 4;
        try {
            rates = ExchangeRateFetcher.fetch();
            Iterator<String> currencyIterator = rates.keySet().iterator();
            while (currencyIterator.hasNext()) {
                String currency = currencyIterator.next();
                spreadsheet.getRange(startingRow, 0, 1, 2).setValues(currency, rates.get(currency));
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
            if (range.getWorksheet().getSheetId().equals(selectSheet.getSheetId())
                    && range.getRow() < 4 + rates.size()) {
                destinationCurrency = spreadsheet.getRange(range.getRow(), 0).getValue().toString();
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

        //listen to buy another
        listSheet.getButton("BuyAnother").addAction(buttonShapeMouseEvent -> {
            selectSheet.activate();
        });
    }

    private void placeAnOrder() {
        listSheet.unprotect("");
        Double cost = spreadsheet.getRange(BOOK_NAME, exchangeSheet.getSheetId(), "B3").getRangeValue().getCellValue().getDoubleValue();
        Double amount = spreadsheet.getRange(BOOK_NAME, exchangeSheet.getSheetId(), "E3").getRangeValue().getCellValue().getDoubleValue();
        Range range = spreadsheet.getRange(BOOK_NAME, listSheet.getSheetId(), "A3:D3");
        range.insert(Range.InsertShiftDirection.ShiftDown, Range.InsertFormatOrigin.LeftOrAbove);
        range.setValues(cost, destinationRate, destinationCurrency, amount);
        listSheet.protect(PROTECTION);
    }

    private void fillDestinationCurrencyRate() {
        exchangeSheet.unprotect("");
        spreadsheet.getRange(BOOK_NAME, exchangeSheet.getName(), "E4").setValue(destinationRate);
        spreadsheet.getRange(BOOK_NAME, exchangeSheet.getName(), "F4").setValue(destinationCurrency);
        spreadsheet.getRange(BOOK_NAME, exchangeSheet.getName(), "B3").activate();
        listSheet.protect(PROTECTION);
    }
}
