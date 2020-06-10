package io.keikai.devref.advanced.permission;

import io.keikai.api.*;
import io.keikai.api.model.Sheet;
import io.keikai.ui.*;
import io.keikai.ui.event.StartEditingEvent;
import io.keikai.ui.event.Events;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;

import java.util.*;

/**
 * Because most features are available by default except UI visibility, we assign restrictions on each role.
 */
public class AuthorityService {

	static private List<Role> roles = new LinkedList<Role>();
	 // Define roles here
	public static final Role OWNER = new Role(Role.Name.OWNER);
	public static final Role EDITOR = new Role(Role.Name.EDITOR);
	public static final Role VIEWER = new Role(Role.Name.VIEWER);
	public static final EventListener<StartEditingEvent> CANCEL_EDIT_LISTENER = new EventListener<StartEditingEvent>() {
		@Override
		public void onEvent(StartEditingEvent event) throws Exception {
			event.cancel();
			Clients.showNotification("It's read-only", Clients.NOTIFICATION_TYPE_WARNING, null,
					"middle_center", 500);
		}
	};
	private static String CTRL_KEY = "ctrlKey";

	 // Initialize role - restriction relationship
	static{
		EDITOR.assign(new Restriction(Restriction.NAME.SHEET_ADD) {
			
			@Override
			void apply(Spreadsheet ss) {
				ss.disableUserAction(AuxAction.ADD_SHEET, true);
			}

			@Override
			void clear(Spreadsheet ss) {
				ss.disableUserAction(AuxAction.ADD_SHEET, false);				
			}
		});
		
		EDITOR.assign(new Restriction(Restriction.NAME.SHEET_DELETE) {
			
			@Override
			void apply(Spreadsheet ss) {
				ss.disableUserAction(AuxAction.DELETE_SHEET, true);
			}

			@Override
			void clear(Spreadsheet ss) {
				ss.disableUserAction(AuxAction.DELETE_SHEET, false);				
			}
		});
		
		EDITOR.assign(new Restriction(Restriction.NAME.SHEET_MOVE) {
			
			@Override
			void apply(Spreadsheet ss) {
				ss.disableUserAction(AuxAction.MOVE_SHEET_LEFT, true);
				ss.disableUserAction(AuxAction.MOVE_SHEET_RIGHT, true);
			}

			@Override
			void clear(Spreadsheet ss) {
				ss.disableUserAction(AuxAction.MOVE_SHEET_LEFT, false);
				ss.disableUserAction(AuxAction.MOVE_SHEET_RIGHT, false);				
			}
		});
		
		EDITOR.assign(new Restriction(Restriction.NAME.SHEET_HIDE) {
			
			@Override
			void apply(Spreadsheet ss) {
				ss.disableUserAction(AuxAction.HIDE_SHEET, true);
				ss.disableUserAction(AuxAction.UNHIDE_SHEET, true);
			}

			@Override
			void clear(Spreadsheet ss) {
				ss.disableUserAction(AuxAction.HIDE_SHEET, false);
				ss.disableUserAction(AuxAction.UNHIDE_SHEET, false);				
			}
		});
		
		EDITOR.assign(new Restriction(Restriction.NAME.SHEET_RENAME) {
			
			@Override
			void apply(Spreadsheet ss) {
				ss.disableUserAction(AuxAction.RENAME_SHEET, true);
			}

			@Override
			void clear(Spreadsheet ss) {
				ss.disableUserAction(AuxAction.RENAME_SHEET, false);				
			}
		});
		
		EDITOR.assign(new Restriction(Restriction.NAME.SHEET_COPY) {
			
			@Override
			void apply(Spreadsheet ss) {
				ss.disableUserAction(AuxAction.COPY_SHEET, true);
			}

			@Override
			void clear(Spreadsheet ss) {
				ss.disableUserAction(AuxAction.COPY_SHEET, false);				
			}
		});
		
		EDITOR.assign(new Restriction(Restriction.NAME.SHEET_PROTECT) {
			
			@Override
			void apply(Spreadsheet ss) {
				ss.disableUserAction(AuxAction.PROTECT_SHEET, true);
			}

			@Override
			void clear(Spreadsheet ss) {
				ss.disableUserAction(AuxAction.PROTECT_SHEET, false);				
			}
		});
		
		for (Restriction p : EDITOR.getRestrictions()){
			VIEWER.assign(p);
		}
		VIEWER.assign(new Restriction(Restriction.NAME.TOOLBAR) {
			
			@Override
			void apply(Spreadsheet ss) {
				ss.setShowToolbar(false);
			}

			@Override
			void clear(Spreadsheet ss) {
				ss.setShowToolbar(true);				
			}
		});
		VIEWER.assign(new Restriction(Restriction.NAME.FORMULABAR) {

			@Override
			void apply(Spreadsheet ss) {
				ss.setShowFormulabar(false);
			}

			@Override
			void clear(Spreadsheet ss) {
				ss.setShowFormulabar(true);				
			}
		});
		VIEWER.assign(new Restriction(Restriction.NAME.CONTEXT_MENU) {

			@Override
			void apply(Spreadsheet ss) {
				ss.setShowContextMenu(false);
			}

			@Override
			void clear(Spreadsheet ss) {
				ss.setShowContextMenu(true);
			}
		});
		SheetProtection READ_ONLY_WITH_SELECTION = SheetProtection.Builder.create().
				withSelectLockedCellsAllowed(true).withSelectLockedCellsAllowed(true).build();
		VIEWER.assign(new Restriction(Restriction.NAME.EDIT) {
			
			@Override
			void apply(Spreadsheet ss) {
				String shareScope = ss.getBook().getShareScope();
				if (shareScope == null || shareScope.equals(EventQueues.DESKTOP)){
					for (int i=0 ; i < ss.getBook().getNumberOfSheets() ; i++){
						Ranges.range(ss.getBook().getSheetAt(i)).protectSheet(READ_ONLY_WITH_SELECTION);
					}
				}else{
					/*
					 * When a book is shared, protecting sheets will affect other collaborators.
					 * Need to avoid editing at the component level.
					 */
					ss.addEventListener(Events.ON_START_EDITING, CANCEL_EDIT_LISTENER);
					ss.setAttribute(CTRL_KEY, ss.getCtrlKeys());
					ss.setCtrlKeys("");
				}
			}

			@Override
			void clear(Spreadsheet ss) {
				String shareScope = ss.getBook().getShareScope();
				if (shareScope == null || shareScope.equals(EventQueues.DESKTOP)){
					for (int i=0 ; i < ss.getBook().getNumberOfSheets() ; i++){
						Ranges.range(ss.getBook().getSheetAt(i)).unprotectSheet("");
					}
				}else{
					/*
					 * When a book is shared, protecting sheets will affect other collaborators.
					 * Need to avoid editing at the component level.
					 */
					ss.removeEventListener(Events.ON_START_EDITING, CANCEL_EDIT_LISTENER);
					String ctrlKeys = (String)ss.getAttribute(CTRL_KEY);
					if (ctrlKeys!= null){
						ss.setCtrlKeys(ctrlKeys);
					}
				}
				
			}
		});
		roles.add(OWNER);
		roles.add(EDITOR);
		roles.add(VIEWER);
	}
	
	static public List<Role> getPredefinedRoles(){
		return Collections.unmodifiableList(roles);
	}
	
	static public void applyRestriction(Spreadsheet ss, Role role){
		checkProtection(ss);
		displayAllUI(ss);
		for (Restriction p : role.getRestrictions()){
			p.apply(ss);
		}
	}
	
	/**
	 * clear all restrictions applied on a Spreadsheet
	 * @param ss
	 */
	static public void clearRestriction(Spreadsheet ss){
		for (Restriction p : VIEWER.getRestrictions()){
			p.clear(ss);
		}
	}

	/**
	 * Some roles has edit permission, a sheet protection conflicts it, so show a warning. 
	 * @param ss
	 */
	public static void checkProtection(Spreadsheet ss) {
		for (int i=0 ; i < ss.getBook().getNumberOfSheets() ; i++){
			Sheet sheet = ss.getBook().getSheetAt(i);
			if (Ranges.range(sheet).isProtected()){
				Clients.showNotification("Sheet "+sheet.getSheetName()+" is protected", Clients.NOTIFICATION_TYPE_WARNING, null,
						"middle_center", 500);
			}
		}
	}

	/**
	 * Since Spreadsheet all UI visibility is false by default, we should make it's visible (no restrictions) at first before applying restrictions.
	 * @param ss
	 */
	private static void displayAllUI(Spreadsheet ss) {
		ss.setShowContextMenu(true);
		ss.setShowFormulabar(true);
		ss.setShowToolbar(true);
		ss.setShowSheetbar(true);
	}
}
