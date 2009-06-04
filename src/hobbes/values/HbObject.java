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
		return show().getValue().toString();
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
	
	@HobbesMethod(name="class",numArgs=0)
	public HbClass getClassInstance() {
		return klass;
	}
	
	@HobbesMethod(name="object_id",numArgs=0)
	public HbInt objectId() {
		return getObjSpace().getInt(id);
	}
	
	@HobbesMethod(name="hash_code",numArgs=0)
	public HbInt getHashCode() {
		return objectId();
	}
	
	@HobbesMethod(name="show",numArgs=0)
	public HbString show() {
		StringBuilder repr = new StringBuilder("<");
		repr.append(getClassInstance().getName());
		repr.append(" @ ");
		repr.append(Integer.toHexString(getId()));
		repr.append(">");
		return new HbString(getObjSpace(),repr);
	}
	
}
