package io.keikai.devref.model;

import io.keikai.api.*;
import io.keikai.api.model.*;
import io.keikai.model.CellRegion;
import io.keikai.ui.Spreadsheet;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.Label;

import java.io.*;

public class TraverseCells {

    public static void main(String[] args) throws IOException {
        Book book = Importers.getImporter().imports(new File("src/main/webapp/WEB-INF/books/autofill.xlsx"), "book.xlsx");
        Sheet sheet = book.getSheetAt(0);
        CellRegion dataRegion = Ranges.range(sheet).getDataRegion();

        Ranges.range(sheet, new AreaRef(dataRegion.getReferenceString())).zOrderStream()
                .filter(range -> !range.getCellData().isBlank())
                .forEach(range -> {
                    System.out.println(range);
                });
    }

}
