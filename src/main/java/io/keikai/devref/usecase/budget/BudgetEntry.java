package io.keikai.devref.usecase.budget;

import java.util.List;

/**
 * represents one row in a budget table.
 */
public class BudgetEntry {

	private String label; //expense category
	private String dept;
	private List<Number> periods;
	
	public BudgetEntry(String label, String dept, List<Number> periods) {
		this.label = label;
		this.dept = dept;
		this.periods = periods;
	}
	public String getLabel() {
		return label;
	}
	public String getDept() {
		return dept;
	}
	public List<Number> getPeriods() {
		return periods;
	}
}
