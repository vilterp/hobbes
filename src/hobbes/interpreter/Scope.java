package hobbes.interpreter;

import hobbes.values.HbValue;

import java.util.HashMap;
import java.util.HashSet;

public class Scope {
	
	private ObjectSpace objSpace;
	private HashMap<String,Integer> names;
	private HashSet<String> globals;
	
	private static HashSet<String> readOnly = new HashSet<String>();
	static {
		readOnly.add("true");
		readOnly.add("false");
		readOnly.add("nil");
	}
	
	public Scope(ObjectSpace o) {
		this(o,null);
	}
	
	public Scope(ObjectSpace o, Scope adoptGlobals) {
		objSpace = o;
		names = new HashMap<String,Integer>();
		if(adoptGlobals == null) {
			globals = new HashSet<String>();
			addGlobals();
		} else {
			globals = adoptGlobals.getGlobals();
			for(String global: adoptGlobals.getGlobals()) {
				try {
					setGlobalForce(global,adoptGlobals.get(global));
				} catch (UndefinedNameException e) {
					throw new IllegalArgumentException("name \"" + global
														+ "\" in globals not in scope object");
				}
			}
		}
	}
	
	private void addGlobals() {
		setGlobalForce("true", objSpace.getTrue());
		setGlobalForce("false", objSpace.getFalse());
		setGlobalForce("nil", objSpace.getNil());
	}
	
	public HashSet<String> getGlobals() {
		return globals;
	}
	
	public HbValue get(String name) throws UndefinedNameException {
		if(names.containsKey(name))
			return objSpace.get(names.get(name));
		else
			throw new UndefinedNameException(name);
	}
	
	public void set(String name, HbValue val) throws ReadOnlyNameException {
		// get id of whatever is already there
		Integer prevId = null;
		if(names.containsKey(name))
			prevId = names.get(name);
		// ensure this name is not read-only
		if(readOnly.contains(name))
			throw new ReadOnlyNameException(name);
		// set
		names.put(name, val.getId());
		// garbage collect on overwritten object
		if(prevId != null)
			objSpace.garbageCollect(prevId);
	}
	
	// can be used to set read-only names
	public void setForce(String name, HbValue val) {
		try {
			set(name,val);
		} catch (ReadOnlyNameException e) {}
	}
	
	public void setGlobal(String name, HbValue val) throws ReadOnlyNameException {
		set(name,val);
		globals.add(name);
	}
	
	// can be used to set read-only globals
	public void setGlobalForce(String name, HbValue val) {
		try {
			setGlobal(name,val);
		} catch (ReadOnlyNameException e) {}
	}
	
	public void delete(String name) {
		int id = names.remove(name);
		objSpace.garbageCollect(id);
	}
	
	public boolean isDefined(String name) {
		return names.containsKey(name);
	}
	
}
