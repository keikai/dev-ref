package io.keikai.devref.usecase;

import io.keikai.api.*;
import io.keikai.api.model.Sheet;
import io.keikai.ui.Spreadsheet;
import io.keikai.ui.event.*;
import org.apache.poi.ss.usermodel.DateUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;

import java.io.IOException;
import java.util.*;

import static io.keikai.devref.util.RangeHelper.*;

public class ExchangeComposer extends SelectorComposer {
    @Wire("spreadsheet")
    private Spreadsheet spreadsheet;
    final private static String SELECT_SHEET = "select";
    final private static String EXCHANGE_SHEET = "exchange";
    final private static String LIST_SHEET = "list";

    private Map<String, Double> rates; //currency : exchange rate
    private String destinationCurrency;
    private static final SheetProtection VIEW_ONLY = SheetProtection.Builder.create().withSelectLockedCellsAllowed(true).withSelectUnlockedCellsAllowed(true).withAutoFilterAllowed(true).build();


    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        displayExchangeRate();
        protectAllSheets();
    }

    @Listen(Events.ON_CELL_CLICK + "=spreadsheet")
    public void onCellClick(CellMouseEvent e) {
        String sheetName = e.getSheet().getSheetName();
        switch (sheetName) {
            case SELECT_SHEET:
                selectCurrency(e);
                break;
            case EXCHANGE_SHEET:
                buy(e);
                break;
            case LIST_SHEET:
                buyAnother(e);
                break;
        }
    }

    private void buyAnother(CellMouseEvent e){
        Range buyAnotherButton = Ranges.rangeByName(e.getSheet(), "buyAnother");
        if (isRangeClicked(e, buyAnotherButton)){
            spreadsheet.setSelectedSheet(SELECT_SHEET);
        }
    }

    private void buy(CellMouseEvent e) {
        Range buyButton = Ranges.rangeByName(e.getSheet(), "buy");
        if (isRangeClicked(e, buyButton)) {
            double cost = Ranges.rangeByName(e.getSheet(), "cost").getCellData().getDoubleValue();
            double amount = Ranges.rangeByName(e.getSheet(), "amount").getCellData().getDoubleValue();
            if (cost > 0) {
                spreadsheet.setSelectedSheet(LIST_SHEET);
                placeAnOrder(cost, amount);
            }
        }
    }

    private void placeAnOrder(double cost, double amount) {
        Sheet sheet = spreadsheet.getBook().getSheet(LIST_SHEET);
        insert1stRow(sheet);
        //locate the 1st cell based on the header because insertion causes a name range shifted
        Range firstCell1stRow = Ranges.rangeByName(sheet, "header").toShiftedRange(1,0);
        Range fillCell = firstCell1stRow;
        setValuesInRow(firstCell1stRow, DateUtil.getExcelDate(new Date()), cost, destinationCurrency,
                rates.get(destinationCurrency), amount);
    }

    /**
     * insert at 1st row. the text style copied from the header, so we need to copy data row style back to 1st row.
     * @param sheet
     */
    private void insert1stRow(Sheet sheet) {
        Range firstRow = Ranges.rangeByName(sheet, "header").toShiftedRange(1,0).toRowRange();
        firstRow.insert(Range.InsertShift.DOWN, Range.InsertCopyOrigin.FORMAT_LEFT_ABOVE);
        firstRow.toShiftedRange(1, 0).paste(firstRow);
    }

    private void selectCurrency(CellMouseEvent e) {
        Range currencyTable = Ranges.rangeByName(e.getSheet(), "currencyTable");
        if (isRangeClicked(e, currencyTable)) {
            destinationCurrency = Ranges.range(e.getSheet(), e.getRow(), 6).getCellValue().toString();
            spreadsheet.setSelectedSheet(EXCHANGE_SHEET);
            fillDestinationCurrencyRate();
        }
    }

    private void fillDestinationCurrencyRate() {
        Sheet selectedSheet = spreadsheet.getSelectedSheet();
        Ranges.rangeByName(selectedSheet, "currency").setCellValue(destinationCurrency);
        Ranges.rangeByName(selectedSheet, "rate").setCellValue(rates.get(destinationCurrency));
        Range cost = Ranges.rangeByName(selectedSheet, "cost");
        spreadsheet.focusTo(cost.getRow(), cost.getColumn());
    }

    private void displayExchangeRate() {
        spreadsheet.focus();
        int startingRow = 8;
        try {
            rates = ExchangeRateFetcher.fetch();
            for (String currency : ExchangeRateFetcher.interested_currency_list) {
                Ranges.range(spreadsheet.getSelectedSheet(), startingRow, 6).setCellValue(currency);
                Ranges.range(spreadsheet.getSelectedSheet(), startingRow, 6 + 1).setCellValue(rates.get(currency));
                startingRow++;
            }
        } catch (IOException e) {
            Ranges.range(spreadsheet.getSelectedSheet(), startingRow, 0).setCellValue(e.getMessage());
        }
    }

    private void protectAllSheets() {
        for (int i = 0; i < spreadsheet.getBook().getNumberOfSheets(); i++) {
            Ranges.range(spreadsheet.getBook().getSheetAt(i)).protectSheet(VIEW_ONLY);
        }
    }
}
