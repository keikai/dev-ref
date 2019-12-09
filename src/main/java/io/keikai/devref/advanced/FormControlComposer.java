package io.keikai.devref.advanced;

import java.util.*;

import io.keikai.api.Ranges;
import io.keikai.ui.Spreadsheet;
import io.keikai.ui.event.CellMouseEvent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.*;

/**
 * @author Hawk
 */
public class FormControlComposer extends SelectorComposer<Component> {

	private static final long serialVersionUID = 1L;
	private Map<String, String> controlsName = new HashMap<String, String>();
	@Wire
	private Spreadsheet ss;
	@Wire
	private Popup popup;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		controlsName.put("✔", "checked");
		controlsName.put("◻", "box");
		controlsName.put("✈", "airplane");
		controlsName.put("✕", "cross");
		controlsName.put("★", "star");
		controlsName.put("✜", "plus");
		controlsName.put("✪", "star shield");
	}

	@Listen("onCellClick = #ss")
	public void print(CellMouseEvent e){
		String controlSymbol = Ranges.range(e.getSheet(),e.getRow(), e.getColumn()).getCellEditText();
		String name = controlsName.get(controlSymbol);
		if (name != null){
			Ranges.range(e.getSheet(), e.getRow(), e.getColumn()+1).setCellEditText(name);
		}else{
			popup.open(e.getClientx(),e.getClienty());
		}
	}
}



