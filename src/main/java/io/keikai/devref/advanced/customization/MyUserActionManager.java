package io.keikai.devref.advanced.customization;

import io.keikai.ui.impl.*;
import io.keikai.ui.impl.ua.ClearCellHandler;
import io.keikai.ui.impl.undo.ClearCellAction;
import io.keikaiex.ui.impl.UserActionManagerCtrlImpl;
import org.zkoss.zk.ui.event.KeyEvent;

public class MyUserActionManager extends UserActionManagerCtrlImpl {

    private static final String BACKSPACE_CODE = "#bak";

    public MyUserActionManager() {
        super();
        ClearCellHandler handler = new ClearCellHandler(ClearCellAction.Type.CONTENT);
        registerHandler(Category.KEYSTROKE.getName(), "#del", handler);
        registerHandler(Category.KEYSTROKE.getName(), BACKSPACE_CODE, handler);
    }

    /**
     * to support backspace, <code>#bak</code>
     * https://www.zkoss.org/wiki/ZK_Developer%27s_Reference/UI_Patterns/Keystroke_Handling
     * @return
     */
    public String getCtrlKeys() {
        return XUtils.getDoubleVersion() >= 8.5 ? "^Z%Z^Y%Y^X%X^C%C^V%V^B%B^I%I^U%U#del"+BACKSPACE_CODE : "^Z^Y^X^C^V^B^I^U#del";
    }

    @Override
    protected String getAction(KeyEvent event) {
        if(event.getKeyCode() == 8){ //backspace, #bak
            return BACKSPACE_CODE;
        }
        return super.getAction(event);
    }
}
