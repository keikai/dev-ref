package io.keikai.devref.advanced.embed;

import io.keikai.ui.Spreadsheet;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;

import java.util.Map;

public class EmbeddedSpreadsheetComposer extends SelectorComposer {
    @Wire
    private Spreadsheet spreadsheet;

    @Listen("onImport = #spreadsheet")
    public void onImport(Event event){
        String fileName = ((Map)event.getData()).get("file").toString();
        spreadsheet.setSrc("/WEB-INF/books/" + fileName);
    }

    @Listen("onToggleVisibility = #spreadsheet")
    public void onToggleVisibility(Event event){
        String ui = ((Map)event.getData()).get("ui").toString();

        switch (ui){
            case "toolbar":
                spreadsheet.setShowToolbar(!spreadsheet.isShowToolbar());
                break;
            case "sheetbar":
                spreadsheet.setShowSheetbar(!spreadsheet.isShowSheetbar());
                break;
            case "formulabar":
                spreadsheet.setShowFormulabar(!spreadsheet.isShowFormulabar());
                break;
        }
    }
}
