package org.zkoss.zss.essential.advanced.permission;

import java.util.Collection;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModelList;

public class LoginComposer extends SelectorComposer<Component> {

	static Collection<Role> roles = AuthorityService.getPredefinedRoles();

	@Wire("combobox")
	Combobox roleBox;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		ListModelList<Role> model = new ListModelList<Role>(roles.toArray(new Role[0]));
		model.addToSelection(model.get(0));
		roleBox.setModel(model);
	}
	
	@Listen("onClick = button")
	public void login(){
		Executions.getCurrent().getSession().setAttribute("role", roleBox.getSelectedItem().getValue());
		Executions.getCurrent().sendRedirect("main.zul");
	}
}
