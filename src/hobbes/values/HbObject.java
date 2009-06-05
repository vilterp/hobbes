package hobbes.values;

import java.util.HashMap;

import hobbes.interpreter.ObjectSpace;

@HobbesClass(name="Object")
public class HbObject extends Throwable {
	
	private int id;
	private HbClass klass;
	private ObjectSpace objSpace;
	private HashMap<String,Integer> instanceVars;
	
	public HbObject(ObjectSpace o) {
		objSpace = o;
		id = objSpace.add(this);
		instanceVars = new HashMap<String,Integer>();
		// get HbClass instance
		if(getClass().isAnnotationPresent(HobbesClass.class)) {
			String className = getClass().getAnnotation(HobbesClass.class).name();
			if(!className.equals("Class"))
				klass = getObjSpace().getClass(className);
		} else
			throw new IllegalArgumentException("\"" + getClass().getName()
					+ "\" extends HbObject but doesn't have a HbClass annotation");
	}
	
	public HbObject(ObjectSpace o, HbClass c) {
		// FIXME eww code duplication
		objSpace = o;
		id = objSpace.add(this);
		instanceVars = new HashMap<String,Integer>();
		klass = c;
	}
	
	public void setClass(HbClass c) {
		klass = c;
	}
	
	public String toString() {
		StringBuilder repr = new StringBuilder("<");
		repr.append(getHbClass().getName());
		repr.append(" @ ");
		repr.append(getId());
		repr.append(">");
		return repr.toString();
	}
	
	@HobbesMethod(name="toString",numArgs=0)
	public HbString hbToString() {
		return new HbString(getObjSpace(),toString());
	}

	@HobbesMethod(name="hash_code")
	public HbInt hbHashCode() {
		return getObjSpace().getInt(hashCode());
	}

	public int hashCode() {
		return getId();
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
	
	@HobbesMethod(name="init")
	public void init() {}
	
	@HobbesMethod(name="class")
	public HbClass getHbClass() {
		return klass;
	}
	
	@HobbesMethod(name="object_id")
	public HbInt objectId() {
		return new HbInt(getObjSpace(),id);
	}
	
}
