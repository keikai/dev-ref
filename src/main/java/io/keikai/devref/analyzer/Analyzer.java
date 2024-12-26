package io.keikai.devref.analyzer;

import io.keikai.api.Importers;
import io.keikai.api.model.*;
import io.keikai.devref.analyzer.counter.*;
import io.keikai.model.*;
import org.zkoss.lang.Library;

import java.io.*;
import java.util.*;

import static java.lang.System.exit;

/**
 * Iterate cells and generate a statistics report including the number of:
 * - sheets
 * - non-empty cells, formulas
 * - TODO cells of each sheet
 * - name ranges
 * - TODO images, charts
 * - TODO data validation
 */
public class Analyzer {
    static private File file;
    static private Book book;
    static private Counter cellCounter;
    static private Counter sheetCounter;
    static private Counter<Book> bookCounter;

    public static void main(String[] args) throws IOException {
        setup();
        checkArguments(args);
        importFile();
        setupCounter();
        analyze();
        generateReport();
    }

    private static void importFile() throws IOException {
        System.out.println("import " + file.getAbsolutePath());
        book = Importers.getImporter().imports(file, file.getName());
    }

    private static void setupCounter() {
        bookCounter = new StyleCounter(null);
        sheetCounter = new SheetCounter(
                new NameCounter(null));
        cellCounter = new CellCounter(
                new FormulaCounter(null));
    }

    private static void analyze() {
        bookCounter.process(book);
        for (int i = 0; i < book.getNumberOfSheets(); i++) {
            Sheet sheet = book.getSheetAt(i);
            sheetCounter.process(sheet);
            Iterator<SRow> iterator = sheet.getInternalSheet().getRowIterator();
            while (iterator.hasNext()) {
                Iterator<SCell> cellIterator = iterator.next().getCellIterator();
                while (cellIterator.hasNext()) {
                    cellCounter.process(cellIterator.next());
                }
            }
        }
    }

    private static void checkArguments(String[] args) {
        if (args.length < 1) {
            System.out.println("a file with full path is required");
            exit(1);
        }
        file = new File(args[0]);
        if (!file.exists()) {
            System.out.println(file.getAbsolutePath() + " not found!");
            return;
        }
    }

    private static void generateReport() {
        System.out.println("----------------");
        System.out.println("Analyzer Result");
        System.out.println("----------------");
        printReportItem(bookCounter);
        printReportItem(sheetCounter);
        printReportItem(cellCounter);
    }

    private static void printReportItem(Counter counter) {
        Counter currentCounter = counter;
        while (currentCounter != null) {
            ReportItem item = new ReportItem(currentCounter);
            System.out.printf("%15s : %s\n", item.getName(), item.getResult());
            currentCounter = currentCounter.getNext();
        }
    }

    public static Counter getCellCounter() {
        return cellCounter;
    }

    public static Counter getSheetCounter() {
        return sheetCounter;
    }

    public static Counter<Book> getBookCounter() {
        return bookCounter;
    }

    private static void setup() {
        Properties props = new Properties();
        try {
            props.load(Analyzer.class.getResourceAsStream("/zss.test.properties"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Iterator<?> it = props.keySet().iterator(); it.hasNext(); ) {
            String key = (String) it.next();
            Library.setProperty(key, props.getProperty(key));
        }
    }
}
