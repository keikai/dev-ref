package io.keikai.devref.usecase;

import io.keikai.api.*;
import io.keikai.api.model.Book;
import io.keikai.ui.Spreadsheet;
import io.keikai.ui.event.CellMouseEvent;
import io.keikai.ui.event.Events;
import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ReportGeneratorController extends SelectorComposer {
    @Wire("spreadsheet")
    private Spreadsheet spreadsheet;
    // template name : imported Book
    private static Map<String, Book> templateMap = new HashMap();
    private static final File DEFAULT_TEMPLATE_FOLDER = new File(WebApps.getCurrent().getRealPath("/WEB-INF/books/report/"));
    private boolean atLeast1TemplateSelected = false;
    // https://en.wikipedia.org/wiki/Check_mark
    private static String NOT_CHECKED = "\uD83D\uDDF8";
    private static String CHECKED = "✓";
    static AreaRef checkMarks = new AreaRef("C6:C9");
    static AreaRef buildButton = new AreaRef("E11");
    private Range table;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        table = Ranges.rangeByName(spreadsheet.getSelectedSheet(), "FormTable");
        importTemplates();
    }

    private void importTemplates() throws IOException {
        if (templateMap.size() > 0){
            return; // already imported
        }
        int row = table.getRow();
        int lastRow = table.getLastRow();
        int templateColumn = table.getLastColumn();
        for ( ; row <= lastRow ; row++) {
            String name = Ranges.range(spreadsheet.getSelectedSheet(), row, templateColumn).getCellValue().toString() ;
            Book templateBook = Importers.getImporter().imports(new File(DEFAULT_TEMPLATE_FOLDER, name+ ".xlsx"), name);
            templateMap.put(name, templateBook);
        }
    }

    @Listen(Events.ON_CELL_CLICK + "= spreadsheet")
    public void onCellClick(CellMouseEvent e){
        if (inCheckMarks(e)) {
            toggleCheckMark(getRange(e));
        }else if (inBuildButton(e)){
            build();
        }
    }

    private void toggleCheckMark(Range checkMarkCell) {
        if (checkMarkCell.getCellValue().equals(NOT_CHECKED)){
            checkMarkCell.setCellValue(CHECKED);
        }else{
            checkMarkCell.setCellValue(NOT_CHECKED);
        }
    }

    private void build() {
        Book newReport = Books.createBook("newReport");
        int row = table.getRow();
        int lastRow = table.getLastRow();
        for ( ; row <= lastRow ; row++) {
            if (isChecked(Ranges.range(spreadsheet.getSelectedSheet(), row, table.getColumn()))){
                String fileName = Ranges.range(spreadsheet.getSelectedSheet(), row, table.getLastColumn()).getCellValue().toString();
                Book template = templateMap.get(fileName);
                Ranges.range(newReport).cloneSheetFrom(template.getSheetAt(0).getSheetName(), template.getSheetAt(0));
            }
        }
        if (newReport.getNumberOfSheets() > 0 ){
            spreadsheet.setBook(newReport);
            enableEditMode();
        }
    }

    private void enableEditMode() {
        spreadsheet.setShowSheetbar(true);
        spreadsheet.setMaxVisibleRows(40);
        spreadsheet.setMaxVisibleColumns(15);
    }


    private boolean isChecked(Range range) {
        return range.getCellValue().toString().equals(CHECKED);
    }

    private static boolean inCheckMarks(CellMouseEvent e) {
        return checkMarks.contains(e.getRow(), e.getColumn(), e.getRow(), e.getColumn());
    }

    private static boolean inBuildButton(CellMouseEvent e) {
        return buildButton.contains(e.getRow(), e.getColumn(), e.getRow(), e.getColumn());
    }

    private static Range getRange(CellMouseEvent e){
        return Ranges.range(e.getSheet(), e.getRow(), e.getColumn(), e.getRow(), e.getColumn());
    }
}
