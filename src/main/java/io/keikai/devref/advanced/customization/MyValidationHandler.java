package io.keikai.devref.advanced.customization;

import io.keikai.api.*;
import io.keikai.api.model.*;
import io.keikai.model.*;
import io.keikai.ui.UserActionContext;
import io.keikai.ui.sys.UndoableActionManager;
import io.keikaiex.ui.dialog.impl.DialogCallbackEvent;
import io.keikaiex.ui.impl.ua.DataValidationHandler;
import io.keikaiex.ui.impl.undo.DataValidationAction;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zul.Messagebox;

import java.util.*;

public class MyValidationHandler extends DataValidationHandler {

    @Override
    protected boolean processAction(final UserActionContext ctx) {
        final Sheet sheet = ctx.getSheet();
        final AreaRef selection = ctx.getSelection();
        Range range = Ranges.range(sheet, selection);
        final List<Validation> validations = range.getValidations();
        //ZSS-990
        validTableTotalRows(range);
        if(isOverlapped(range)) {
            String label = Labels.getLabel("zssex.dlg.format.dataValidation.overlapMsg");
            Messagebox.show(label, "ZK Spreadsheet",
                    Messagebox.OK + Messagebox.CANCEL, Messagebox.INFORMATION, new SerializableEventListener<Event>() {
                        private static final long serialVersionUID = 2936000638773070248L;

                        @Override
                        public void onEvent(Event event) throws Exception {
                            if(event.getData().equals(Messagebox.OK)) {
                                showValidationDialog(ctx, sheet, selection, validations);
                            }
                        }
                    });
        } else {
            showValidationDialog(ctx, sheet, selection, validations);
        }
        return true;
    }

}
