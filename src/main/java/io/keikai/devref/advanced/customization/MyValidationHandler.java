package io.keikai.devref.advanced.customization;

import io.keikai.api.AreaRef;
import io.keikai.api.model.*;
import io.keikai.ui.UserActionContext;
import io.keikaiex.ui.impl.ua.DataValidationHandler;

import java.util.List;

/**
 * A dummy example that shows you can extend an existing {@link io.keikai.ui.UserActionHandler}
 */
public class MyValidationHandler extends DataValidationHandler {

    @Override
    protected void showValidationDialog(UserActionContext ctx, Sheet sheet, AreaRef selection, List<Validation> validations) {
        super.showValidationDialog(ctx, sheet, selection, validations);
    }
}
