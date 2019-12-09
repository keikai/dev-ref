package io.keikai.devref.advanced.permission;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * Role is a job function or title which defines an authority level. One role could have one or more restrictions.
 * This class should be immutable to avoid being changed for security concern.
 * @author hawk
 *
 */
public class Role {

	public enum Name {OWNER, EDITOR, VIEWER};
	
	private Name name;
	private Set<Restriction> restrictions = new TreeSet<Restriction>();
	
	public Role(Name name){
		this.name = name;
	}
	
	public Name getName() {
		return name;
	}
	
	void assign(Restriction p){
		restrictions.add(p);
	}

	Set<Restriction> getRestrictions() {
		return Collections.unmodifiableSet(restrictions);
	}
	
	
}
