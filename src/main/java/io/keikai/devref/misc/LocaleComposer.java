package io.keikai.devref.misc;

import io.keikai.ui.Spreadsheet;
import org.zkoss.util.Locales;
import org.zkoss.web.Attributes;
import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.*;

import java.util.*;


/**
 * Set locale of ZSS.
 * Please read https://www.zkoss.org/wiki/ZK_Developer%27s_Reference/Internationalization/Locale for details.
 * @author Hawk
 */
@SuppressWarnings("serial")
public class LocaleComposer extends SelectorComposer<Component> {

    @Wire
    private Spreadsheet ss;
    @Wire
    private Combobox localeBox;

    private ListModelList localeModel;
    static Locale[] allLocales = Locale.getAvailableLocales();

    static {
        Arrays.sort(allLocales, new Comparator() {
            public int compare(Object o1, Object o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
    }

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);  //wire variables and event listeners
        localeModel = new ListModelList(allLocales);
        localeBox.setModel(localeModel);

        localeModel.addToSelection(Locales.getCurrent());
    }

    /**
     * edit text of a date cell is determined by BuiltinFormats.getBuiltinFormat(14, locale)
     */
    @Listen("onSelect = combobox")
    public void selectLocale() {
        Locale locale = (Locale) localeModel.getSelection().iterator().next();
        Sessions.getCurrent().setAttribute(Attributes.PREFERRED_LOCALE,locale);
        Executions.getCurrent().sendRedirect(null); //reload page to render in new locale
    }
}
