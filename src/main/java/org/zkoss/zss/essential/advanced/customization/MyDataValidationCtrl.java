package org.zkoss.zss.essential.advanced.customization;

import io.keikai.api.model.Validation;
import io.keikai.ui.Spreadsheet;
import io.keikaiex.ui.dialog.DataValidationCtrl;
import io.keikaiex.ui.dialog.impl.DialogCallbackEvent;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.*;

import java.util.Map;

public class MyDataValidationCtrl extends DataValidationCtrl {

    public MyDataValidationCtrl(){
        this.allowListmodel = new ListModelList(AllowPair.values());
    }

    public static void show(EventListener<DialogCallbackEvent> callback, Validation validation, Spreadsheet ss) {
        Map arg = newArg(callback);
        arg.put("validationCode", validation);
        arg.put("spreadsheet", ss);
        Window comp = (Window) Executions.createComponents("/WEB-INF/dialog/dataValidation.zul", (Component)null, arg);
        comp.doModal();
    }

    static enum AllowPair {
//        DECIMAL(Labels.getLabel("zssex.dlg.format.dataValidation.settings.allow.decimal"), Validation.ValidationType.DECIMAL, true, true, false, Labels.getLabel("zssex.dlg.format.dataValidation.settings.value"), Labels.getLabel("zssex.dlg.format.dataValidation.settings.minimum"), Labels.getLabel("zssex.dlg.format.dataValidation.settings.maximum"), true, true),
//        DATE(Labels.getLabel("zssex.dlg.format.dataValidation.settings.allow.date"), Validation.ValidationType.DATE, true, true, false, Labels.getLabel("zssex.dlg.format.dataValidation.settings.date"), Labels.getLabel("zssex.dlg.format.dataValidation.settings.startDate"), Labels.getLabel("zssex.dlg.format.dataValidation.settings.endDate"), true, true),
//        TIME(Labels.getLabel("zssex.dlg.format.dataValidation.settings.allow.time"), Validation.ValidationType.TIME, true, true, false, Labels.getLabel("zssex.dlg.format.dataValidation.settings.time"), Labels.getLabel("zssex.dlg.format.dataValidation.settings.startTime"), Labels.getLabel("zssex.dlg.format.dataValidation.settings.endTime"), true, true),
//        TEXT_LENGTH(Labels.getLabel("zssex.dlg.format.dataValidation.settings.allow.textLength"), Validation.ValidationType.TEXT_LENGTH, true, true, false, Labels.getLabel("zssex.dlg.format.dataValidation.settings.length"), Labels.getLabel("zssex.dlg.format.dataValidation.settings.minimum"), Labels.getLabel("zssex.dlg.format.dataValidation.settings.maximum"), true, true),
//        CUSTOM(Labels.getLabel("zssex.dlg.format.dataValidation.settings.allow.custom"), Validation.ValidationType.CUSTOM, false, true, false, Labels.getLabel("zssex.dlg.format.dataValidation.settings.formula"), "", "", true, false);
        ANY_VALUE(Labels.getLabel("zssex.dlg.format.dataValidation.settings.allow.anyValue"), Validation.ValidationType.ANY, false, false, false, "", "", "", false, false),
        WHOLE_NUMBER(Labels.getLabel("zssex.dlg.format.dataValidation.settings.allow.wholeNumber"), Validation.ValidationType.INTEGER, true, true, false, Labels.getLabel("zssex.dlg.format.dataValidation.settings.value"), Labels.getLabel("zssex.dlg.format.dataValidation.settings.minimum"), Labels.getLabel("zssex.dlg.format.dataValidation.settings.maximum"), true, true),
        LIST(Labels.getLabel("zssex.dlg.format.dataValidation.settings.allow.list"), Validation.ValidationType.LIST, false, true, true, Labels.getLabel("zssex.dlg.format.dataValidation.settings.source"), "", "", true, false);

        String title;
        Validation.ValidationType type;
        boolean dataEnabled;
        boolean ignoreBlankEnabled;
        boolean inCellCheckEnabled;
        String pointName;
        String minimumName;
        String maximumName;
        boolean param1Enabled;
        boolean param2Enabled;

        private AllowPair(String title, Validation.ValidationType type, boolean dataEnabled, boolean ignoreBlankEnabled, boolean inCellCheckEnabled, String pointName, String minimumName, String maximumName, boolean param1Enabled, boolean param2Enabled) {
            this.title = title;
            this.type = type;
            this.dataEnabled = dataEnabled;
            this.ignoreBlankEnabled = ignoreBlankEnabled;
            this.inCellCheckEnabled = inCellCheckEnabled;
            this.pointName = pointName;
            this.minimumName = minimumName;
            this.maximumName = maximumName;
            this.param1Enabled = param1Enabled;
            this.param2Enabled = param2Enabled;
        }

        public static AllowPair getAllowPair(Validation.ValidationType type) {
            AllowPair[] var1 = values();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                AllowPair pair = var1[var3];
                if (pair.type == type) {
                    return pair;
                }
            }

            return null;
        }

        public Validation.ValidationType getType() {
            return this.type;
        }

        public boolean isDataEnabled() {
            return this.dataEnabled;
        }

        public boolean isIgnoreBlankEnabled() {
            return this.ignoreBlankEnabled;
        }

        public boolean isInCellCheckEnabled() {
            return this.inCellCheckEnabled;
        }

        public String getPointName() {
            return this.pointName;
        }

        public String getMinimumName() {
            return this.minimumName;
        }

        public String getMaximumName() {
            return this.maximumName;
        }

        public boolean isParam1Enabled() {
            return this.param1Enabled;
        }

        public boolean isParam2Enabled() {
            return this.param2Enabled;
        }

        public String toString() {
            return this.title;
        }
    }
}
