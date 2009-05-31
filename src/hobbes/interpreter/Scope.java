package hobbes.interpreter;

import hobbes.values.HbNativeFunction;
import hobbes.values.HbInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Scope {
	
	private ObjectSpace objSpace;
	private HashMap<String,Integer> names;
	private HashSet<String> globals;
	
	private static HashSet<String> readOnlys = new HashSet<String>();
	static {
		readOnlys.add("true");
		readOnlys.add("false");
		readOnlys.add("nil");
		readOnlys.add("print");
		readOnlys.add("get_input");
	}
	
	public Scope(ObjectSpace o) {
		this(o,null);
	}
	
	public Scope(ObjectSpace o, Scope inheritGlobalsFrom) {
		objSpace = o;
		names = new HashMap<String,Integer>();
		if(inheritGlobalsFrom == null) {
			globals = new HashSet<String>();
		} else {
			globals = inheritGlobalsFrom.getGlobals();
			for(String global: inheritGlobalsFrom.getGlobals()) {
				try {
					setGlobalForce(global,inheritGlobalsFrom.get(global));
				} catch (UndefinedNameException e) {
					throw new IllegalArgumentException("name \"" + global
										+ "\" in globals not in scope object");
				}
			}
		}
	}
	
	public void addBasics() {
		setGlobalForce("true", objSpace.getTrue());
		setGlobalForce("false", objSpace.getFalse());
		setGlobalForce("nil", objSpace.getNil());
		// print function
		setGlobalForce("print",new HbNativeFunction(objSpace,"print",new String[]{"object"}));
		// get_input function
		setGlobalForce("get_input",
						new HbNativeFunction(objSpace,"get_input",new String[]{"prompt"}));
	}
	
	public HashSet<String> getGlobals() {
		return globals;
	}
	
	public HbInstance get(String name) throws UndefinedNameException {
		if(names.containsKey(name))
			return objSpace.get(names.get(name));
		else
			throw new UndefinedNameException(name);
	}
	
	public void set(String name, HbInstance val) throws ReadOnlyNameException {
		// get id of whatever is already there
		Integer prevId = null;
		if(names.containsKey(name))
			prevId = names.get(name);
		// ensure this name is not read-only
		if(readOnlys.contains(name))
			throw new ReadOnlyNameException(name);
		// set
		names.put(name, val.getId());
		// garbage collect on overwritten object
		if(prevId != null)
			objSpace.garbageCollect(prevId);
	}
	
	public void setGlobal(String name, HbInstance val) throws ReadOnlyNameException {
		if(readOnlys.contains(name))
			throw new ReadOnlyNameException(name);
		else {
			names.put(name, val.getId());
			globals.add(name);
		}
	}
	
	public void setGlobalForce(String name, HbInstance val) {
		names.put(name, val.getId());
		globals.add(name);
	}
	
	public void delete(String name) throws ReadOnlyNameException {
		if(readOnlys.contains(name))
			throw new ReadOnlyNameException(name);
		else {
			int id = names.remove(name);
			objSpace.garbageCollect(id);
		}
	}
	
	public boolean isDefined(String name) {
		return names.containsKey(name);
	}
	
}
