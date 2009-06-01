package hobbes.values;

import java.util.HashMap;

import hobbes.interpreter.ObjectSpace;

public abstract class HbObject {
	
	private int id;
	private HbClass theClass;
	private ObjectSpace objSpace;
	private HashMap<String,Integer> instanceVars;
	private HashMap<String,HbMethod> methods;
	
	public HbObject(ObjectSpace o) {
		objSpace = o;
		id = objSpace.add(this);
		theClass = objSpace.getBuiltinClass(getClass().getName().substring(2));
		instanceVars = new HashMap<String,Integer>();
		methods = new HashMap<String,HbMethod>();
	}
	
	public String toString() {
		return show().getValue().toString();
	}
	
	public int getId() {
		return id;
	}
	
	public ObjectSpace getObjSpace() {
		return objSpace;
	}
	
	public void putInstVar(String name, HbObject val) {
		instanceVars.put(name, val.getId());
	}
	
	public HbObject getInstVar(String name) {
		return objSpace.get(instanceVars.get(name));
	}
	
	@HobbesMethod(name="class",numArgs=0)
	public HbClass getClassInstance() {
		return theClass;
	}
	
	@HobbesMethod(name="object_id",numArgs=0)
	public HbInt objectId() {
		return getObjSpace().getInt(id);
	}
	
	public abstract HbString show();
	
}
