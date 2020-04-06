package io.keikai.devref.jsf;

import javax.faces.bean.*;
import javax.faces.context.FacesContext;

import io.keikai.ui.event.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.util.Clients;

@ManagedBean(name= "myController")
@RequestScoped
public class MyController extends SelectorComposer<Component>{
	private static final long serialVersionUID = 1L;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		// import an xlsx file
		// manipulate Book object for initialization
		System.out.println("initialize cell data");
	}

	@Listen(Events.ON_STOP_EDITING + " = spreadsheet")
    public void onStopEditing(StopEditingEvent event){
		FacesContext context = FacesContext.getCurrentInstance();
		Clients.showNotification("edited");
	}
}
