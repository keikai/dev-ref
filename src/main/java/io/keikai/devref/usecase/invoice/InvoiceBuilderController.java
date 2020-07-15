package io.keikai.devref.usecase.invoice;

import io.keikai.api.*;
import io.keikai.api.SheetProtection;
import io.keikai.api.model.*;
import io.keikai.devref.util.*;
import io.keikai.ui.*;
import io.keikai.ui.event.*;
import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zul.*;

import java.io.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class InvoiceBuilderController extends SelectorComposer<Component> {

    //defined names
    private static final String PRODUCTS = "products";
    private static final String CUSTOMERS = "customers";
    private static final String PRODUCT_TABLE = "ProductTable";
    @Wire
    private Spreadsheet spreadsheet;
    @Wire
    private Groupbox templateBox;

    private Image selectedPreview;

    private static final String TEMPLATE_KEY = "template";
    private static final String SELECTED = "selected";
    final private static String CHECKED = "☑";
    final private static String NOT_CHECKED = "☐";
    private String[] templateFileNameList = {"invoice-template1.xlsx", "invoice-template2.xlsx"};
    private List<Map<String, String>> selectedCustomers = new LinkedList();
    private List<Map<String, String>> selectedProducts = new LinkedList();

    final private static SheetProtection SELECTION_FILTER = SheetProtection.Builder.create()
            .withSelectLockedCellsAllowed(true)
            .withSelectUnlockedCellsAllowed(true)
            .withAutoFilterAllowed(true).build();
    private Range customerTable;
    private Range productTable;
    // key is template file name
    private static HashMap<String, Book> templates = new HashMap<>();

    private static Importer importer = Importers.getImporter();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        buildTemplatePreview();
        initializeRange();
        populateCustomers();
        limitAccess();
        importInvoiceTemplate();
    }

    /**
     * assume templateFileNameList could be loaded from another sources
     */
    private void buildTemplatePreview() {
        List<Image> images = new ArrayList<>();
        Arrays.stream(templateFileNameList).forEach(fileName -> {
            String fileNameWithoutExt = fileName.substring(0, fileName.lastIndexOf('.'));
            Image preview = new Image(fileNameWithoutExt + "-preview.jpg");
            templateBox.appendChild(preview);
            preview.setAttribute(TEMPLATE_KEY, fileName);
            preview.addEventListener(org.zkoss.zk.ui.event.Events.ON_CLICK, event ->
                    selectTemplate((Image) event.getTarget()));
            images.add(preview);
        });
        //default selection
        selectTemplate(images.get(0));
    }

    /**
     * import templates asynchronously to avoid blocking page loading
     */
    private void importInvoiceTemplate() {
        CompletableFuture.runAsync(() -> {
            try {
                for (String template : templateFileNameList) {
                    if (!templates.containsKey(template)) { //avoid importing again
                        templates.put(template
                                , importer.imports(new File(WebApps.getCurrent().getRealPath(BookUtil.DEFAULT_BOOK_FOLDER)
                                        , template), template));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void initializeRange() {
        customerTable = Ranges.rangeByName(spreadsheet.getBook().getSheet(CUSTOMERS), "CustomerTable");
        productTable = Ranges.rangeByName(spreadsheet.getBook().getSheet(PRODUCTS), PRODUCT_TABLE);
    }

    private void limitAccess() {
        for (int i = 0; i < spreadsheet.getBook().getNumberOfSheets(); i++) {
            Ranges.range(spreadsheet.getBook().getSheetAt(i)).protectSheet(SELECTION_FILTER);
        }
        spreadsheet.disableUserAction(AuxAction.ADD_SHEET, true);
    }

    private void populateCustomers() {

        List<String[]> customers = CustomerService.getCustomerList();
        Range startingCell = customerTable.toCellRange(0, 1); //the 1st column is for checkbox
        for (String[] c : customers) {
            RangeHelper.setValuesInRow(startingCell, c);
            startingCell = startingCell.toShiftedRange(1, 0);
        }
    }

    public void selectTemplate(Image preview) {
        if (selectedPreview != null) {
            selectedPreview.removeSclass(SELECTED);
        }
        preview.setSclass(SELECTED);
        selectedPreview = preview;
    }

    private String getSelectedTemplateFileName() {
        return selectedPreview.getAttribute(TEMPLATE_KEY).toString();
    }

    @Listen(Events.ON_CELL_CLICK + "= spreadsheet")
    public void onCellClick(CellMouseEvent e) {
        Range clickedCell = RangeHelper.getTargetRange(e);
        String sheetName = e.getSheet().getSheetName();
        switch (sheetName) {
            case CUSTOMERS:
            case PRODUCTS:
                if (isCheckmark(clickedCell.getCellValue().toString())) {
                    toggleCheckMark(clickedCell);
                }
        }
    }

    @Listen(org.zkoss.zk.ui.event.Events.ON_CLICK + "=#create")
    public void createInvoice() {
        processSelection(selectedCustomers, customerTable);
        //ignore the last column - Amount column
        Range productData = Ranges.range(productTable.getSheet()
                , productTable.getRow(), productTable.getColumn()
                , productTable.getLastRow(), productTable.getLastColumn() - 1);
        processSelection(selectedProducts, productData);
        if (!validateSelection()) {
            return;
        }

        Book invoiceBook = Books.createBook("invoice.xlsx");
        for (Map customer : selectedCustomers) {
            Sheet invoiceSheet = Ranges.range(invoiceBook).cloneSheetFrom(customer.get("CompanyName").toString()
                    , templates.get(getSelectedTemplateFileName()).getSheetAt(0));
            generateAgentData();
            populateNamedRange(generateAgentData(), invoiceSheet);
            populateNamedRange(customer, invoiceSheet);
            populateProducts(selectedProducts, invoiceSheet);
        }

        spreadsheet.setMaxVisibleRows(27);
        spreadsheet.setMaxVisibleColumns(8);
        spreadsheet.setBook(invoiceBook);
    }

    /**
     * Populate selected products to the named range {@link #PRODUCT_TABLE}
     */
    private void populateProducts(List<Map<String, String>> selectedProducts, Sheet sheet) {
        Range table = Ranges.rangeByName(sheet, PRODUCT_TABLE);
        Range startingCell = table.toCellRange(0, 0);
        for (Map product : selectedProducts) {
            RangeHelper.setValuesInRow(startingCell, product.values().toArray());
            startingCell = startingCell.toCellRange(1, 0);
        }

    }

    private Map<String, Object> generateAgentData() {
        Map<String, Object> invoice = new HashMap<>();
        invoice.put("AgentName", "Hawk");
        invoice.put("AgentPhone", "333-5555");
        invoice.put("AgentEmail", "hawk@potix.com");

        invoice.put("InvoiceDate", Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        invoice.put("InvoiceNo", generateInvoiceId());

        return invoice;
    }

    private String generateInvoiceId() {
        String timestamp = Long.toString(System.currentTimeMillis());
        return "IV-" + timestamp.substring(timestamp.length() - 6);
    }

    /**
     * Each key in the specified fieldMap represents a named range, populate its value to the corresponding named range in the specified sheet
     */
    private void populateNamedRange(Map<String, Object> fieldMap, Sheet sheet) {
        List<String> namedRanges = Ranges.getNames(sheet);
        fieldMap.forEach((name, value) -> {
            if (namedRanges.contains(name)) {
                Range range = Ranges.rangeByName(sheet, name);
                range.setCellValue(value);
            }
        });
    }


    private boolean validateSelection() {
        if (validateSelection(selectedCustomers, "You  don't select any customer")) return false;
        if (validateSelection(selectedProducts, "You  don't select any product")) return false;
        return true;
    }

    private boolean validateSelection(List selectedItems, String s) {
        if (selectedItems.size() == 0) {
            Notification.show(s, Notification.TYPE_ERROR, spreadsheet, "middle_center", 1000);
            return true;
        }
        return false;
    }

    private void processSelection(List selectedRows, Range table) {
        selectedRows.clear();
        selectedRows.addAll(extractRow(table));
    }

    /**
     * extract a row as a map. the key is header value, the value is the corresponding cell value.
     * For example, {Name: Debra, Phone: 338-8777, ...}
     */
    private List<Map> extractRow(Range dataRange) {
        List<Map> selected = new LinkedList();
        //ignore the 1st column - the checkbox.
        Range header = Ranges.range(dataRange.getSheet(), dataRange.getRow() - 1, dataRange.getColumn() + 1, dataRange.getRow() - 1, dataRange.getLastColumn());
        for (int i = 0; i < dataRange.getRowCount(); i++) {
            Range checkMark = dataRange.toCellRange(i, 0);
            if (isChecked(checkMark.getCellValue().toString())) {
                Range oneRow = Ranges.range(dataRange.getSheet()
                        , dataRange.getRow() + i, dataRange.getColumn() + 1
                        , dataRange.getRow() + i, dataRange.getLastColumn());
                HashMap<String, String> fieldMap = new LinkedHashMap(); //header value (named range) : each cell value
                for (int col = 0; col < header.getColumnCount(); col++) {
                    String field = header.toCellRange(0, col).getCellValue().toString();
                    String value = oneRow.toCellRange(0, col).getCellValue().toString();
                    fieldMap.put(field, value);
                }
                selected.add(fieldMap);
            }
        }
        return selected;
    }

    private boolean isCheckmark(String value) {
        return value.equals(CHECKED) || value.equals(NOT_CHECKED);
    }

    private void toggleCheckMark(Range checkMarkCell) {
        if (checkMarkCell.getCellValue().equals(NOT_CHECKED)) {
            checkMarkCell.setCellValue(CHECKED);
        } else {
            checkMarkCell.setCellValue(NOT_CHECKED);
        }
    }

    private boolean isChecked(String value) {
        return value.equals(CHECKED);
    }
}
