package io.keikai.devref.jsf;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import io.keikai.ui.event.StopEditingEvent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.util.Clients;

@ManagedBean(name= "myController")
@SessionScoped
public class MyController extends SelectorComposer<Component>{
	
	private static final long serialVersionUID = 1L;


	@Listen("onStopEditing = spreadsheet")
    public void onStopEditing(StopEditingEvent event){
		FacesContext context = FacesContext.getCurrentInstance();
		Clients.showNotification("edited");
	}
	
	
}
