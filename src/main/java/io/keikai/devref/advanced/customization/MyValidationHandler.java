package io.keikai.devref.advanced.customization;

import io.keikai.ui.UserActionContext;
import io.keikaiex.ui.impl.ua.DataValidationHandler;

/**
 * A dummy example that shows you can extend an existing {@link io.keikai.ui.UserActionHandler}
 */
public class MyValidationHandler extends DataValidationHandler {

    //override a method
    @Override
    protected boolean processAction(final UserActionContext ctx) {
        return super.processAction(ctx);
    }

}
