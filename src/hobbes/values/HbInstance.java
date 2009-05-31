package hobbes.values;

import java.util.HashMap;

import hobbes.interpreter.ObjectSpace;

public abstract class HbInstance {
	
	private int id;
	private ObjectSpace objSpace;
	private HashMap<String,Integer> instanceVars;
	
	public HbInstance(ObjectSpace o) {
		objSpace = o;
		id = objSpace.add(this);
		instanceVars = new HashMap<String,Integer>();
	}
	
	public int getId() {
		return id;
	}
	
	public ObjectSpace getObjSpace() {
		return objSpace;
	}
	
	public HbBoolean toBool() {
		return getObjSpace().getTrue();
	}
	
	public String toString() {
		return show().toString();
	}
	
	public abstract HbString show();
	public abstract HbString getType();
	public abstract HbBoolean is(HbInstance other);
	
}
