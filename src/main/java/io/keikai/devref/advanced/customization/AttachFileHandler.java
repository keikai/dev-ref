package io.keikai.devref.advanced.customization;

import io.keikai.api.model.*;
import io.keikai.ui.*;
import org.zkoss.zk.ui.util.Notification;

public class AttachFileHandler implements UserActionHandler {
    @Override
    public boolean process(UserActionContext ctx) {
        System.out.println(ctx.getAction());
        Notification.show(ctx.getSheet().getSheetName() + "!" + ctx.getSelection());
        System.out.println("data:" + ctx.getData("mydata"));
        return true;
    }

    @Override
    public boolean isEnabled(Book book, Sheet sheet) {
        return true;
    }
}
