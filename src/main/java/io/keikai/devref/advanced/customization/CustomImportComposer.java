package io.keikai.devref.advanced.customization;

import io.keikai.api.Importers;
import io.keikai.devref.util.BookUtil;
import io.keikai.ui.Spreadsheet;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;

public class CustomImportComposer extends SelectorComposer {

    @Wire
    private Spreadsheet spreadsheet;

    public CustomImportComposer() {
        super();
//        BookUtil.overrideDefaultImporter(FirstSheetImporter::new);
        BookUtil.registerCustomImporter("first", FirstSheetImporter::new);
    }

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        String fileName = "Payroll.xlsx";
        spreadsheet.setBook(Importers.getImporter("first").imports(BookUtil.getFile(fileName), fileName));
    }

}
