package io.keikai.devref.jsf;

import io.keikai.ui.Spreadsheet;
import io.keikai.ui.event.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zk.ui.util.Clients;

import javax.faces.bean.*;
import javax.faces.context.FacesContext;

@ManagedBean(name = "myController")
@RequestScoped
public class MyController extends SelectorComposer<Component> {
    private static final long serialVersionUID = 1L;

    /**
     * refer to {@link SelectorComposer} for more syntax of component selector
     **/
    @Wire("spreadsheet")
    private Spreadsheet spreadsheet;

    /**
     * Initialize keikai in this method, e.g. import an xlsx file and manipulate Book object for initialization,
	 * set spreadsheet's properties.
     */
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        spreadsheet.setShowFormulabar(false);
        spreadsheet.setShowToolbar(false);
        spreadsheet.setShowSheetbar(false);
        spreadsheet.setMaxVisibleRows(15);
        spreadsheet.setMaxVisibleColumns(9);
    }

    @Listen(Events.ON_STOP_EDITING + " = spreadsheet")
    public void onStopEditing(StopEditingEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        Clients.showNotification("edited");
    }
}
