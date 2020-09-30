package io.keikai.devref;

import io.keikai.api.Ranges;
import io.keikai.ui.Spreadsheet;
import org.zkoss.poi.ss.usermodel.ZssContext;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.*;

import java.util.*;

/**
 * Set locale of ZSS
 * @author Hawk
 *
 */
@SuppressWarnings("serial")
public class LocaleComposer extends SelectorComposer<Component> {

	@Wire
	private Spreadsheet ss;
	@Wire
	private Combobox localeBox;
	@Wire
	private Label localeLabel;

	private ListModelList localeModel;
	static Locale[] allLocales = Locale.getAvailableLocales();
	static{
		Arrays.sort(allLocales, new Comparator(){
			public int compare(Object o1, Object o2){
				return o1.toString().compareTo(o2.toString());
			}
		});
	}
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);  //wire variables and event listeners
		localeModel = new ListModelList(allLocales);
		localeBox.setModel(localeModel);

		localeModel.addToSelection(ZssContext.getCurrent().getLocale());
	}

	@Listen("onSelect = combobox")
	public void selectLocale(){
		Locale locale = (Locale)localeModel.getSelection().iterator().next();
		ZssContext.setThreadLocal(new ZssContext(locale, -1));
		Ranges.range(ss.getSelectedSheet()).notifyChange();
	}

}
