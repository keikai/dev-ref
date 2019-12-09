package org.zkoss.zss.essential.advanced.permission;

import io.keikai.ui.Spreadsheet;
import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.A;

public class PermissionComposer extends SelectorComposer<Component> {
	@Wire
	private Spreadsheet ss;
	@Wire("a")
	private A logoutLink;
	private Role loginRole;
	
	@Override
	public ComponentInfo doBeforeCompose(Page page, Component parent,
			ComponentInfo compInfo) {
		loginRole = (Role)Executions.getCurrent().getSession().getAttribute("role");
		//prevent unauthorized access, we can use Initiator to achieve the same effect
		if (loginRole == null){
			Executions.getCurrent().sendRedirect("login.zul"); 
			return null;
		}
		return super.doBeforeCompose(page, parent, compInfo);
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		logoutLink.setLabel(logoutLink.getLabel()+" - "+loginRole.getName());
		AuthorityService.applyRestriction(ss, loginRole); //apply the role's permission
	}
}
