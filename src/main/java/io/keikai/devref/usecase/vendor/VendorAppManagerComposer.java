package io.keikai.devref.usecase.vendor;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Tabbox;

import io.keikai.api.AreaRef;
import io.keikai.api.CellOperationUtil;
import io.keikai.api.Range;
import io.keikai.api.Range.InsertCopyOrigin;
import io.keikai.api.Range.InsertShift;
import io.keikai.api.Ranges;
import io.keikai.api.SheetProtection;
import io.keikai.api.model.CellStyle;
import io.keikai.api.model.Font.Boldweight;
import io.keikai.api.model.Font.Underline;
import io.keikai.api.model.Sheet;
import io.keikai.devref.usecase.vendor.persistence.PersistenceUtil;
import io.keikai.devref.usecase.vendor.persistence.VendorMap;
import io.keikai.ui.Spreadsheet;
import io.keikai.ui.event.CellMouseEvent;
import io.keikai.ui.event.Events;

public class VendorAppManagerComposer extends SelectorComposer<Component>{

	final private static String MANAGER_SHEET = "Sheet1";
	private static final SheetProtection VIEW_ONLY = SheetProtection.Builder.create().withSelectLockedCellsAllowed(true).withSelectUnlockedCellsAllowed(true).withAutoFilterAllowed(true).build();
	@Wire("#spreadsheetManager")
	private Spreadsheet spreadsheetManager;

	@Wire
	private Tabbox tabbox;
	
	/* columns and column names mapping */
	public final static Map<String, String> DISPLAY_COLUMN_NAMES;
	static {
		DISPLAY_COLUMN_NAMES = new HashMap<String, String>();
		DISPLAY_COLUMN_NAMES.put("businessCategory1", "Business Category 1");
		DISPLAY_COLUMN_NAMES.put("businessCategory2", "Business Category2");
		DISPLAY_COLUMN_NAMES.put("businessProductsAndServices", "Business Products And Services");
		DISPLAY_COLUMN_NAMES.put("companyAdress", "Company Adress");
		DISPLAY_COLUMN_NAMES.put("companyName", "Company Name");
		DISPLAY_COLUMN_NAMES.put("contact1Designation", "Contact 1 Designation");
		DISPLAY_COLUMN_NAMES.put("contact1Name", "Contact 1 Name");
		DISPLAY_COLUMN_NAMES.put("contact1phone", "Contact 1 Phone");
		DISPLAY_COLUMN_NAMES.put("contact2Designation", "Contact 2 Designation");
		DISPLAY_COLUMN_NAMES.put("contact2Name", "Contact 2 Name");
		DISPLAY_COLUMN_NAMES.put("contact2phone", "Contact 2 phone");
		DISPLAY_COLUMN_NAMES.put("contact3Designation", "Contact 3 Designation");
		DISPLAY_COLUMN_NAMES.put("contact3Name", "Contact 3 Name");
		DISPLAY_COLUMN_NAMES.put("contact3phone", "Contact 3 phone");
		DISPLAY_COLUMN_NAMES.put("country", "country");
		DISPLAY_COLUMN_NAMES.put("email", "email");
		DISPLAY_COLUMN_NAMES.put("financialDetailsYear1production", "Financial Details Year 1 production");
		DISPLAY_COLUMN_NAMES.put("financialDetailsYear1turnover", "Financial Details Year 1 turnover");
		DISPLAY_COLUMN_NAMES.put("financialDetailsYear1year", "Financial Details Year 1 year");
		DISPLAY_COLUMN_NAMES.put("financialDetailsYear2production", "Financial Details Year 2 production");
		DISPLAY_COLUMN_NAMES.put("financialDetailsYear2turnover", "Financial Details Year 2 turnover");
		DISPLAY_COLUMN_NAMES.put("financialDetailsYear2year", "Financial Details Year 2 year");
		DISPLAY_COLUMN_NAMES.put("financialDetailsYear3production", "Financial Details Year 3 production");
		DISPLAY_COLUMN_NAMES.put("financialDetailsYear3turnover", "Financial Details Year 3 turnover");
		DISPLAY_COLUMN_NAMES.put("financialDetailsYear3year", "Financial Details Year 3 year");
		DISPLAY_COLUMN_NAMES.put("legalStructure", "Legal Structure");
		DISPLAY_COLUMN_NAMES.put("phone", "phone");
		DISPLAY_COLUMN_NAMES.put("signatureDate", "Signature Date");
		DISPLAY_COLUMN_NAMES.put("signatureDesignation", "Signature Designation");
		DISPLAY_COLUMN_NAMES.put("signaturePrintName", "Signature PrintName");
		DISPLAY_COLUMN_NAMES.put("signatureSign", "Signature Sign");
		DISPLAY_COLUMN_NAMES.put("website", "website");
	}

	

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        vendorDataManagerToWorkbook();
    }

    @Listen(Events.ON_CELL_CLICK + "=#spreadsheetManager")
    public void onCellClick(CellMouseEvent e) {
        String sheetName = e.getSheet().getSheetName();
        switch (sheetName) {
            case MANAGER_SHEET :
            	if(e.getRow() > 0 && e.getRow() <= PersistenceUtil.getAllVendors().length && e.getColumn() == 3) {
					String cellValue = (String) Ranges.range(spreadsheetManager.getBook().getSheet(MANAGER_SHEET), e.getRow(), e.getColumn()).getCellValue();
            		displayClientView(cellValue);
				}
                break;
        }
    }
	

	private void displayClientView(String cellValue) {
		Executions.getCurrent().sendRedirect("./vendorClient.zul?vendorId="+cellValue);
	}

	private void vendorDataManagerToWorkbook() {
		/*retrieve all vendors objects from persistence*/
		VendorMap[] allVendors = PersistenceUtil.getAllVendors();
		System.out.println(allVendors.length + " vendors to display");
		spreadsheetManager.setSrc(null);
		spreadsheetManager.setSrc("/WEB-INF/book/vendor-app-manager.xlsx");
		Sheet worksheet = spreadsheetManager.getBook().getSheet("Sheet1");
		/*retrieve rows from the worksheet*/
		Range firstrow = Ranges.range(worksheet, new AreaRef("A1")).toRowRange();
		Range displayTable = Ranges.range(worksheet, new AreaRef("A2")).toRowRange();
		/*retrive column names, and prepare them into an array for easy iteration*/
		String[] rangeNames = DISPLAY_COLUMN_NAMES.keySet().toArray(new String[] {});
		for (int i = 0; i < rangeNames.length; i++) {
			firstrow.toCellRange(firstrow.getRow(), i).setCellValue(DISPLAY_COLUMN_NAMES.get(rangeNames[i]));
		}
		int currentRow = 0;
		/* loop on vendor objects. For each vendor, create a new row in the table, and fill each column with the relevant value*/
		for (VendorMap vendor : allVendors) {
			CellOperationUtil.insert(displayTable.toCellRange(currentRow,0).toRowRange(), InsertShift.DOWN, InsertCopyOrigin.FORMAT_LEFT_ABOVE);
			for (int i = 0; i < rangeNames.length; i++) {
				Range currentCell = displayTable.toCellRange(currentRow, i);
				currentCell.setCellValue(vendor.getVendorData().get(rangeNames[i]));
				if(rangeNames[i].equals("companyName")) {
					CellOperationUtil.applyFontBoldweight(currentCell, Boldweight.BOLD);
					CellOperationUtil.applyFontUnderline(currentCell, Underline.SINGLE);
				}
			}
			currentRow++;
		}
		displayTable.toShiftedRange(displayTable.getLastRow(), 0);
	}

	private void protectAllSheets(Spreadsheet spreadsheet) {
        for (int i = 0; i < spreadsheet.getBook().getNumberOfSheets(); i++) {
            Ranges.range(spreadsheet.getBook().getSheetAt(i)).protectSheet(VIEW_ONLY);
        }
    }
    
    
}