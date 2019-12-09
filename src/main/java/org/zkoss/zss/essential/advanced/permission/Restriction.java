package org.zkoss.zss.essential.advanced.permission;


import io.keikai.ui.Spreadsheet;

/**
 * A restriction for a Spreadsheet feature.
 *  This class should be immutable to avoid being changed for security concern. 
 * @author hawk
 *
 */
abstract public class Restriction implements Comparable<Restriction> {
	public enum NAME{TOOLBAR, FORMULABAR, CONTEXT_MENU, SHEETBAR, 
		SHEET_ADD, SHEET_DELETE, SHEET_MOVE, SHEET_RENAME, SHEET_HIDE, SHEET_COPY, SHEET_PROTECT,
		EDIT}
	
	protected NAME name;
	
	public NAME getName() {
		return name;
	}
	
	
	public Restriction(NAME name){
		this.name = name;
	}
	
	/**
	 * Apply the feature restriction on a Spreadsheet
	 */
	abstract void apply(Spreadsheet ss);
	
	/**
	 * clear the feature restriction on a Spreadsheet
	 */
	abstract void clear(Spreadsheet ss);
	
	@Override
	public int compareTo(Restriction p) {
		return this.name.compareTo(p.getName());
	}
}
