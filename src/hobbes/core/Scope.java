package hobbes.core;

import hobbes.values.HbValue;

import java.util.HashMap;
import java.util.HashSet;

public class Scope {
	
	private ObjectSpace objSpace;
	private HashMap<String,Integer> names;
	
	private static HashSet<String> readOnly = new HashSet<String>();
	static {
		readOnly.add("true");
		readOnly.add("false");
		readOnly.add("nil");
	}
	
	public Scope(ObjectSpace o) {
		objSpace = o;
		names = new HashMap<String,Integer>();
		// globals
		names.put("true", objSpace.getTrue().getId());
		names.put("false", objSpace.getFalse().getId());
		names.put("nil", objSpace.getNil().getId());
	}
	
	public HbValue get(String name) throws UndefinedNameException {
		if(names.containsKey(name))
			return objSpace.get(names.get(name));
		else
			throw new UndefinedNameException(name);
	}
	
	public void set(String name, HbValue val) throws ReadOnlyNameException {
		if(readOnly.contains(name))
			throw new ReadOnlyNameException(name);
		else
			names.put(name, val.getId());
	}
	
	public void delete(String name) {
		names.remove(name);
	}
	
	public boolean isDefined(String name) {
		return names.containsKey(name);
	}
	
}
