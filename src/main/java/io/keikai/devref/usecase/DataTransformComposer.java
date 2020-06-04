package io.keikai.devref.usecase;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;

import io.keikai.api.Ranges;
import io.keikai.api.SheetProtection;
import io.keikai.ui.Spreadsheet;
import io.keikai.ui.event.CellMouseEvent;
import io.keikai.ui.event.Events;

public class DataTransformComposer extends SelectorComposer<Component>{

    private static final String SIMPLE_DISPLAY_SHEET = "Display1";
	private static final String COMPLEX_DISPLAY_SHEET = "Display2";
	final private static String SOURCE_SHEET = "Source";
	private static final SheetProtection VIEW_ONLY = SheetProtection.Builder.create().withSelectLockedCellsAllowed(true).withSelectUnlockedCellsAllowed(true).withAutoFilterAllowed(true).build();
	@Wire("spreadsheet")
    private Spreadsheet spreadsheet;


    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        protectAllSheets();
    }

    @Listen(Events.ON_CELL_CLICK + "=spreadsheet")
    public void onCellClick(CellMouseEvent e) {
        String sheetName = e.getSheet().getSheetName();
        switch (sheetName) {
            case SOURCE_SHEET :
            	if(e.getRow() == 14 && e.getColumn() == 2)
            		simpleWorkflow();
            	if(e.getRow() == 16 && e.getColumn() == 2)
            		complexWorkflow();
                break;
        }
    }

	private void simpleWorkflow() {
		spreadsheet.setSelectedSheet(SIMPLE_DISPLAY_SHEET);
	}

	private void complexWorkflow() {
		spreadsheet.setSelectedSheet(COMPLEX_DISPLAY_SHEET);
	}
	
    private void protectAllSheets() {
        for (int i = 0; i < spreadsheet.getBook().getNumberOfSheets(); i++) {
            Ranges.range(spreadsheet.getBook().getSheetAt(i)).protectSheet(VIEW_ONLY);
        }
    }
    
    
}