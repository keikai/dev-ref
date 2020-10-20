package io.keikai.devref.usecase.vendor;

import java.util.HashMap;
import java.util.Map;

import io.keikai.api.model.Sheet;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;

import io.keikai.api.Range;
import io.keikai.api.Ranges;
import io.keikai.api.SheetProtection;
import io.keikai.devref.usecase.vendor.persistence.PersistenceUtil;
import io.keikai.devref.usecase.vendor.persistence.VendorMap;
import io.keikai.ui.Spreadsheet;
import io.keikai.ui.event.CellMouseEvent;
import io.keikai.ui.event.Events;

public class VendorAppClientComposer extends SelectorComposer<Component>{

	@Wire("#spreadsheetClient")
	private Spreadsheet spreadsheetClient;

	final private static String SOURCE_SHEET = "Form";
	final private static SheetProtection VIEW_ONLY = SheetProtection.Builder.create().withSelectUnlockedCellsAllowed(true).withAutoFilterAllowed(true).build();
	private Sheet sheet; //main working sheet
	private VendorMap currentVendor;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
		sheet = spreadsheetClient.getBook().getSheet(SOURCE_SHEET);
        //initialize components and data
        String vendorId = Executions.getCurrent().getParameter("vendorId");
		if(vendorId != null && !"null".equals(vendorId)) {
			currentVendor = PersistenceUtil.getVendorByName(vendorId);
			vendorDataToClientWorkbook();
		}else {
			currentVendor = new VendorMap("", new HashMap<String, Object>());
		}
		protectAllSheets();
    }

    @Listen(Events.ON_CELL_CLICK + "=#spreadsheetClient")
    public void onCellClick(CellMouseEvent e) {
        String sheetName = e.getSheet().getSheetName();
        switch (sheetName) {
            case SOURCE_SHEET :
            	if(e.getRow() == 52 && e.getColumn() == 4)
            		workbookDataToVendor();
                break;
        }
    }
	
    private void vendorDataToClientWorkbook() {
		for (Map.Entry<String, Object> entry : currentVendor.getVendorData().entrySet()) {
			Range rangeByName = Ranges.rangeByName(sheet, entry.getKey());
			if(rangeByName != null) {
				rangeByName.setCellValue(entry.getValue());
			}			
		}
	}
	
	private void workbookDataToVendor() {
		String internalName = (String) Ranges.rangeByName(sheet, "companyName").getCellValue();
		if(internalName != null) {
			
			currentVendor = new VendorMap(internalName , new HashMap<String, Object>());
			for (String name: VendorAppManagerComposer.DISPLAY_COLUMN_NAMES.keySet()) {
				Object value =  Ranges.rangeByName(sheet, name).getCellValue();
				if (value!=null && !"".equals(value)) {
					currentVendor.getVendorData().put(name, value);
				}else {
					currentVendor.getVendorData().remove(name);
				}
			}
			PersistenceUtil.addVendor(currentVendor);
			Executions.getCurrent().sendRedirect("./vendorManager.zul");
		}else {
			Clients.alert("Please enter a company name", "Data Error", Clients.NOTIFICATION_TYPE_WARNING);			
		}
	}
	

	private void protectAllSheets() {
        for (int i = 0; i < spreadsheetClient.getBook().getNumberOfSheets(); i++) {
            Ranges.range(spreadsheetClient.getBook().getSheetAt(i)).protectSheet(VIEW_ONLY);
        }
    }
}