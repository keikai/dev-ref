package io.keikai.devref.usecase;

import io.keikai.api.*;
import io.keikai.api.model.Sheet;
import io.keikai.ui.Spreadsheet;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;

public class TabsComposer extends SelectorComposer<Component> {
    @Wire
    private Spreadsheet spreadsheet;
    static final String ACTIVE_COLOR = "#00b0f0";

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        //put initialization logic here
        init();
    }

    /**
     * hide/unhide children sheets
     */
    private void init() {
        int nSheet = spreadsheet.getBook().getNumberOfSheets();
        boolean visible = true;
        for (int i = 0 ; i < nSheet ; i++){
            Sheet sheet = spreadsheet.getBook().getSheetAt(i);
            if (ACTIVE_COLOR.equals(sheet.getInternalSheet().getTabColor())
            && !"Cable TV".equals(sheet.getSheetName())){
                if (sheet.isHidden()){
                    Ranges.range(sheet).setSheetVisible(Range.SheetVisible.VISIBLE);
                    visible = true;
                }else{
                    Ranges.range(sheet).setSheetVisible(Range.SheetVisible.HIDDEN);
                    visible = false;
                }
            }
        }
        if (visible){
            spreadsheet.setSelectedSheet("Spectrum");
        }else{
            spreadsheet.setSelectedSheet("Index");
        }
    }
}
