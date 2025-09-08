package io.keikai.devref.advanced;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import io.keikai.model.*;
import io.keikai.model.impl.pdf.*;

import java.io.*;
import java.util.List;

/**
 * Exporting multiple sheets from a workbook into a single PDF document.
 */
public class MultiSheetPdfExporter extends PdfExporter {
    private static final long serialVersionUID = 1L;

    /**
     * Exports multiple sheets from the workbook to PDF
     * @param workbook The workbook containing the sheets to export
     * @param sheetIndicesToExport List of sheet indices to export (0-based)
     * @param outputStream Stream to write the PDF to
     * @throws IOException If an I/O error occurs
     */
    public void exportSheets(SBook workbook, List<Integer> sheetIndicesToExport, OutputStream outputStream) throws IOException {
        // Create PDF document
        final Document doc = new Document();
        try (AutoClosableDocument document = new AutoClosableDocument(doc)) {
            _writer = PdfWriter.getInstance(doc, outputStream);
            _wb = workbook;

            // Export each sheet
            for (int i = 0; i < sheetIndicesToExport.size(); i++) {
                int sheetIndex = sheetIndicesToExport.get(i);
                //TODO validate sheetIndex is within bounds
                SSheet sheet = workbook.getSheet(sheetIndex);
                // Skip invalid sheet indices
                if (sheet == null) {
                    continue;
                }

                _currentSheetIndex = sheetIndex;

                // Initialize document for first sheet
                if (i == 0) {
                    initDocument(sheet, doc);
                } else {
                    // Add page break between sheets
                    doc.newPage();
                    initDocumentForSheet(sheet, doc);
                }

                exportSheet(sheet, sheetIndex, doc);
            }
        } catch (DocumentException e) {
            throw new IOException(e.getMessage());
        }
    }
}