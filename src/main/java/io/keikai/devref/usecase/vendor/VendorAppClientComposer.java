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

import io.keikai.api.Range;
import io.keikai.api.Ranges;
import io.keikai.api.SheetProtection;
import io.keikai.devref.usecase.vendor.persistence.PersistenceUtil;
import io.keikai.devref.usecase.vendor.persistence.VendorMap;
import io.keikai.ui.Spreadsheet;
import io.keikai.ui.event.CellMouseEvent;
import io.keikai.ui.event.Events;

public class VendorAppClientComposer extends SelectorComposer<Component>{

	final private static String SOURCE_SHEET = "Form";
	private static final SheetProtection VIEW_ONLY = SheetProtection.Builder.create().withSelectUnlockedCellsAllowed(true).withAutoFilterAllowed(true).build();
	@Wire("#spreadsheetClient")
    private Spreadsheet spreadsheetClient;

	@Wire
	private Tabbox tabbox;
	
	private VendorMap currentVendor;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
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
			Range rangeByName = Ranges.rangeByName(spreadsheetClient.getBook().getSheet("Form"), entry.getKey());
			if(rangeByName != null) {
				rangeByName.setCellValue(entry.getValue());
			}			
		}
	}
	
	private void workbookDataToVendor() {
		String internalName = (String) Ranges.rangeByName(spreadsheetClient.getBook().getSheet("Form"), "companyName").getCellValue();
		if(internalName != null) {
			
			currentVendor = new VendorMap(internalName , new HashMap<String, Object>());
			for (String name: VendorAppManagerComposer.DISPLAY_COLUMN_NAMES.keySet()) {
				Object value =  Ranges.rangeByName(spreadsheetClient.getBook().getSheet("Form"), name).getCellValue();
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