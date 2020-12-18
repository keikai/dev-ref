package io.keikai.devref.usecase.embed;

import com.google.gson.Gson;
import io.keikai.api.*;
import io.keikai.api.model.Sheet;
import io.keikai.devref.util.RangeHelper;
import io.keikai.model.impl.pdf.PdfExporter;
import io.keikai.ui.Spreadsheet;
import io.keikai.ui.event.*;
import org.zkoss.json.JSONObject;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Filedownload;

import java.io.*;
import java.util.List;

/**
 * Notice: to maintain consistent event names between client-side and server-side.
 *
 * @author hawk
 */
public class ProductFilterComposer extends SelectorComposer<Component> {

    @Wire
    private Spreadsheet spreadsheet;

    private ProductService productService = new ProductService();
    private Sheet resultSheet;
    private Gson gson = new Gson();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        //implement component initialization logic here
        resultSheet = spreadsheet.getSelectedSheet();
    }

    @Listen("onSearch = spreadsheet")
    public void exportExcel(Event event) {
        FilterCriteria criteria = convertCriteria((JSONObject) event.getData());
        List<Product> result = productService.query(criteria);
        populateResult(result);
    }

    @Listen(Events.ON_CELL_CLICK + " = spreadsheet")
    public void showVendor(CellMouseEvent event){
        //get vendor name
        Range vendorCell = RangeHelper.getTargetRange(event).toRowRange().toCellRange(0, 3);
        String name = vendorCell.getCellData().getStringValue();
        VendorService.Vendor vendor = VendorService.query(name);
        if (vendor != null){
            Clients.evalJavaScript("Controller.showVendor(" + gson.toJson(vendor) + ")");
        }
    }

    /**
     * populate search result into a sheet
     */
    private void populateResult(List<Product> result) {
        Range tableRow = Ranges.rangeByName(resultSheet, "ReportTable"); //the first row under the table header
        //clear previous result
        tableRow.clearContents();
        if (tableRow.getRowCount() > 2) {
            Ranges.range(resultSheet, tableRow.getRow() + 2, tableRow.getColumn(),
                    tableRow.getLastRow(), tableRow.getLastColumn()).toRowRange().delete(Range.DeleteShift.UP);
        }
        tableRow.toCellRange(0, 2).setCellValue("No Result");
        //insert empty row for filling products
        //re-create table range because row deletion and insertion
        Range rowToInsert = Ranges.rangeByName(resultSheet, "ReportTable").toCellRange(1, 0).toRowRange();
        for (int i = 0; i < result.size() - 2; i++) {
            rowToInsert.insert(Range.InsertShift.DOWN, Range.InsertCopyOrigin.FORMAT_LEFT_ABOVE);
            rowToInsert = rowToInsert.toShiftedRange(1, 0);
        }
        //fill searched products
        Range currentRow = Ranges.rangeByName(resultSheet, "ReportTable").toCellRange(0, 0).toRowRange(); //start from the first row
        for (Product p : result) {
            currentRow.setCellValues(p.getId(), p.getCategory(), p.getName(), p.getVendor(), p.getQuantity(), p.getPrice());
            currentRow = currentRow.toShiftedRange(1, 0);
        }
        Ranges.range(resultSheet, "A1:F100").notifyChange(Range.CellAttribute.ALL); // a workaround to refresh the table
    }

    private FilterCriteria convertCriteria(JSONObject jsonObject) {
        return gson.fromJson(jsonObject.toJSONString(), FilterCriteria.class);
    }

    private Exporter exporter = Exporters.getExporter();

    @Listen("onExportExcel = spreadsheet")
    public void exportExcel() throws IOException {
        File file = File.createTempFile(Long.toString(System.currentTimeMillis()), "temp");
        try (FileOutputStream fos = new FileOutputStream(file);) {
            exporter.export(spreadsheet.getBook(), fos);
        }
        Filedownload.save(new AMedia(spreadsheet.getBook().getBookName(), "xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", file, true));
    }

    private PdfExporter pdfExporter = new PdfExporter();

    @Listen("onExportPdf = spreadsheet")
    public void exportPdf() throws IOException {
        File file = File.createTempFile(Long.toString(System.currentTimeMillis()), "temp");
        try (FileOutputStream fos = new FileOutputStream(file);) {
            pdfExporter.export(spreadsheet.getBook().getInternalBook(), file);
        }
        Filedownload.save(new AMedia(spreadsheet.getBook().getBookName().replace(".xlsx", ".pdf"), "pdf", "application/pdf", file, true));
    }

    class FilterCriteria {
        private String category;
        private int min;
        private int max;

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }
    }
}
