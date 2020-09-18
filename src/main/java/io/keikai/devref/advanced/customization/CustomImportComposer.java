package io.keikai.devref.advanced.customization;

import io.keikai.devref.util.BookUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;

public class CustomImportComposer extends SelectorComposer {

    public CustomImportComposer() {
        super();
        BookUtil.overrideDefaultImporter(FirstSheetImporter::new);
    }

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        BookUtil.registerCustomImporter("first", FirstSheetImporter::new);
    }

}
