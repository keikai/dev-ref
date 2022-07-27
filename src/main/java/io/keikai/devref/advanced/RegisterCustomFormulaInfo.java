package io.keikai.devref.advanced;

import io.keikaiex.ui.dialog.*;
import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.util.Initiator;

import java.util.*;

/**
 * show custom functions in "Insert Function" dialog.
 * To add at the application starts, implement http://www.zkoss.org/javadoc/latest/zk/org/zkoss/zk/ui/util/WebAppInit.html
 */
public class RegisterCustomFormulaInfo implements Initiator {
    static final String CUSTOM = "Custom"; //a category name shown in the drop-down list

    @Override
    public void doInit(Page page, Map<String, Object> args) throws Exception {
        addCustomFormulaInfo();
    }
    static public void addCustomFormulaInfo() {
        if (Formulas.getFormulaInfos().get(CUSTOM) != null) {
            List<FormulaMetaInfo> customList = new LinkedList<>();
            customList.add(new FormulaMetaInfo(CUSTOM, "EXCHANGE",
                    "EXCHANGE(price, exchange_rate)", "convert a price into another price with specified exchange_rate", 2, null));
            Formulas.getFormulaInfos().put(CUSTOM, customList);
        }
    }
}
