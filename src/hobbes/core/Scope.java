package hobbes.core;

import hobbes.values.HbValue;

import java.util.HashMap;

public class Scope {
	
	private ObjectSpace objSpace;
	private HashMap<String,Integer> names;
	
	public Scope(ObjectSpace o) {
		objSpace = o;
		names = new HashMap<String,Integer>();
	}
	
	public HbValue get(String name) throws UndefinedNameException {
		if(names.containsKey(name))
			return objSpace.get(names.get(name));
		else
			throw new UndefinedNameException(name);
	}
	
	public void set(String name, HbValue val) {
		names.put(name, val.getId());
	}
	
	public void delete(String name) {
		names.remove(name);
	}
	
	public boolean isDefined(String name) {
		return names.containsKey(name);
	}
	
}
