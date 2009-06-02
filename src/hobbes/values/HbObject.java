package hobbes.values;

import java.lang.reflect.Method;
import java.util.HashMap;

import hobbes.interpreter.ObjectSpace;

@HobbesClass(name="Object")
public abstract class HbObject extends Throwable {
	
	private int id;
	private HbClass theClass;
	private ObjectSpace objSpace;
	private HashMap<String,Integer> instanceVars;
	private HashMap<String,HbMethod> methods;
	
	public HbObject(ObjectSpace o) {
		objSpace = o;
		id = objSpace.add(this);
		String className = getClass().getAnnotation(HobbesClass.class).name();
		if(className.equals("Class"))
			theClass = (HbClass)this;
		else
			theClass = getObjSpace().getClass(className);
		instanceVars = new HashMap<String,Integer>();
		methods = new HashMap<String,HbMethod>();
		// add methods
		for(Method m: getClass().getMethods()) {
			if(m.isAnnotationPresent(HobbesMethod.class)) {
				HobbesMethod ann = m.getAnnotation(HobbesMethod.class);
				String n = ann.name();
				HbNativeMethod meth = new HbNativeMethod(ann.name(),ann.numArgs(),m); 
				methods.put(ann.name(),meth);
			}
		}
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
	
	public HbMethod getMethod(String name) {
		return methods.get(name);
	}
	
	public HbString getString(String str) {
		return new HbString(getObjSpace(),str);
	}
	
	public HbString getString(StringBuilder str) {
		return getString(str.toString());
	}
	
	@HobbesMethod(name="class",numArgs=0)
	public HbClass getClassInstance() {
		return theClass;
	}
	
	@HobbesMethod(name="object_id",numArgs=0)
	public HbInt objectId() {
		return getObjSpace().getInt(id);
	}
	
	@HobbesMethod(name="show",numArgs=0)
	public abstract HbString show();
	
}
