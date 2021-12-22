package io.keikai.devref.advanced;

import io.keikai.api.Ranges;
import io.keikai.ui.*;
import io.keikai.ui.event.ClipboardPasteEvent;
import io.keikai.ui.impl.DefaultUserActionManagerCtrl;
import io.keikai.ui.impl.ua.PasteHandler;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;

/**
 * Disable the default pasting action, listen {@link io.keikai.ui.event.Events#ON_CLIPBOARD_PASTE} to post-process pasting values.
 */
public class CustomPasteComposer extends SelectorComposer {

    @Wire
    private Spreadsheet spreadsheet;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        spreadsheet.getUserActionManager().setHandler(DefaultUserActionManagerCtrl.Category.KEYSTROKE.getName(), "%V", new PasteHandler(){
            private static final long serialVersionUID = 7464764231901959571L;
            @Override
            protected boolean processAction(UserActionContext ctx) {
                UserActionContext.Clipboard cb = ctx.getClipboard();
                //  disable copy-paste among keikai cells, still allow browsers86+ to pasting values from system clipboard
                if(cb==null)
                    return super.processAction(ctx);
                return true;
            }
        });
    }

    /**
     * At onClipboardPaste, keikai already stores user-pasting values into the target cells, so you can get values to process them.
     */
    @Listen("onClipboardPaste = #spreadsheet")
    public void onClipboardPaste(ClipboardPasteEvent event) {
        Ranges.range(event.getSheet(), event.getArea()).zOrderStream().forEach(range -> {
            range.setCellValue("[" + range.getCellValue().toString() + " ]");
        });
    }
}
