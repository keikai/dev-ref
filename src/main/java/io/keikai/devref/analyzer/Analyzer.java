package io.keikai.devref.analyzer;

import io.keikai.api.Importers;
import io.keikai.api.model.*;
import io.keikai.model.*;

import java.io.*;
import java.util.Iterator;

/**
 * Iterate cells and generate a report including:
 * - sheets
 * - non-empty cells, formulas
 *  - TODO cells of each sheet
 * - name ranges
 * - TODO images, charts
 * - TODO data validation
 */
public class Analyzer {
    static private File file;
    static private Book book;
    static private Counter processor;
    static private Counter sheetProcessor;
    static private Counter<Book> bookProcessor;

    public static void main(String[] args) throws IOException {
        checkArguments(args);
        importFile();
        setupProcessor();
        analyze();
        generateReport();
    }

    private static void importFile() throws IOException {
        System.out.println("import " + file.getAbsolutePath());
        book = Importers.getImporter().imports(file, file.getName());
    }

    private static void setupProcessor() {
        bookProcessor = new StyleCounter(null);
        processor = new CellCounter(new FormulaCounter(null));
        sheetProcessor = new SheetCounter(new NameCounter(null));
    }

    private static void analyze() {
        bookProcessor.process(book);
        for (int i = 0 ; i < book.getNumberOfSheets() ; i++){
            Sheet sheet = book.getSheetAt(i);
            sheetProcessor.process(sheet);
            Iterator<SRow> iterator = sheet.getInternalSheet().getRowIterator();
            while (iterator.hasNext()){
                Iterator<SCell> cellIterator = iterator.next().getCellIterator();
                while (cellIterator.hasNext()){
                    processor.process(cellIterator.next());
                }
            }
        }
    }

    private static void checkArguments(String[] args) {
        if (args.length<1){
            System.out.println("file name is required");
        }
        file = new File(args[0]);
        if (!file.exists()){
            System.out.println(file.getAbsolutePath() + " not found!");
            return;
        }
    }

    private static void generateReport() {
        System.out.println("----------------");
        System.out.println("Analyzer Result");
        System.out.println("----------------");
        printReportItem(bookProcessor);
        printReportItem(sheetProcessor);
        printReportItem(processor);
    }

    private static void printReportItem(Counter processor) {
        Counter currentProcessor = processor;
        while (currentProcessor !=null) {
            ReportItem item = currentProcessor.getItem();
            System.out.printf("%s : %s\n", item.getName(), item.getCounter());
            currentProcessor = currentProcessor.getNext();
        }
    }

}
