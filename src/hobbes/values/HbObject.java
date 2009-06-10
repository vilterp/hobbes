package hobbes.values;

import java.util.ArrayList;
import java.util.HashMap;

import hobbes.interpreter.ErrorWrapper;
import hobbes.interpreter.Interpreter;
import hobbes.interpreter.ObjectSpace;

@HobbesClass(name="Object")
public class HbObject extends Throwable {
	
	private int id;
	private HbClass hobbesClass;
	private Interpreter interp;
	private HashMap<String,Integer> instanceVars;
	
	public HbObject(Interpreter i) {
		interp = i;
		id = getObjSpace().add(this);
		instanceVars = new HashMap<String,Integer>();
		// get HbClass instance
		if(getClass().isAnnotationPresent(HobbesClass.class)) {
			String className = getClass().getAnnotation(HobbesClass.class).name();
			if(!className.equals("Class"))
				hobbesClass = getObjSpace().getClass(className);
		} else
			throw new IllegalArgumentException("\"" + getClass().getName()
					+ "\" extends HbObject but doesn't have a HbClass annotation");
	}
	
	public HbObject(Interpreter i, HbClass c) {
		// FIXME eww code duplication
		interp = i;
		id = getObjSpace().add(this);
		instanceVars = new HashMap<String,Integer>();
		hobbesClass = c;
	}
	
	public void setClass(HbClass c) {
		hobbesClass = c;
	}
	
	public ObjectSpace getObjSpace() {
		return interp.getObjSpace();
	}
	
	public Interpreter getInterp() {
		return interp;
	}
	
	public String toString() {
		return "<" + getHbClass().getName() + "@" + getId() + ">";
	}
	
	public boolean gt(HbObject other) throws ErrorWrapper {
		return getInterp().callMethod(this,">",new HbObject[]{other},null)
											== getObjSpace().getTrue();
	}
	
	public boolean lt(HbObject other) throws ErrorWrapper {
		return getInterp().callMethod(this,"<",new HbObject[]{other},null)
											== getObjSpace().getTrue();
	}
	
	public boolean eq(HbObject other) throws ErrorWrapper {
		return getInterp().callMethod(this,"==",new HbObject[]{other},null)
											== getObjSpace().getTrue();
	}
	
	@HobbesMethod(name="clone")
	public HbObject clone() {
		HbObject newObj = new HbObject(getInterp(),getHbClass());
		for(String instanceVar: instanceVars.keySet())
			newObj.putInstVar(instanceVar,getObjSpace().get(instanceVars.get(instanceVar)));
		return newObj;
	}
	
	@HobbesMethod(name="toString",numArgs=0)
	public HbString hbToString() throws ErrorWrapper {
		StringBuilder repr = new StringBuilder("<");
		repr.append(getHbClass().getName());
		repr.append("@");
		repr.append(getId());
		repr.append(">");
		return new HbString(getInterp(),repr.toString());
	}
	
	public String realToString() throws ErrorWrapper {
		return getInterp().callToString(this);
	}
	
	public String show() throws ErrorWrapper {
		return getInterp().show(this);
	}

	@HobbesMethod(name="hash_code")
	public HbInt hbHashCode() {
		return getObjSpace().getInt(getId());
	}
	
	public int realHashCode() throws ErrorWrapper {
		return ((HbInt)getInterp().callMethod(this,"hash_code",
					new HbObject[]{},null)).getValue();
	}
	
	public void incRefs() {
		getObjSpace().incRefs(getId());
	}
	
	public void decRefs() {
		getObjSpace().decRefs(getId());
	}
	
	public int getId() {
		return id;
	}
	
	public void putInstVar(String name, HbObject val) {
		Integer prevId = instanceVars.get(name);
		instanceVars.put(name,val.getId());
		getObjSpace().incRefs(val.getId());
		if(prevId != null) {
			getObjSpace().decRefs(prevId);
			getObjSpace().garbageCollect(prevId);
		}
	}
	
	public HbObject getInstVar(String name) {
		return getObjSpace().get(instanceVars.get(name));
	}
	
	@HobbesMethod(name="init")
	public void init() {}
	
	@HobbesMethod(name="class")
	public HbClass getHbClass() {
		return hobbesClass;
	}
	
	@HobbesMethod(name="object_id")
	public HbInt objectId() {
		return new HbInt(getInterp(),id);
	}
	
	@HobbesMethod(name="methods")
	public HbSet getMethods() throws ErrorWrapper {
		HbSet temp = new HbSet(getInterp());
		for(String methodName: getHbClass().getMethodNames())
			temp.add(new HbString(getInterp(),methodName));
		return temp;
	}
	
	@HobbesMethod(name="is",numArgs=1)
	public HbObject is(HbObject other) {
		return getObjSpace().getBool(getId() == other.getId());
	}
	
	@HobbesMethod(name="toBool")
	public HbObject toBool() {
		return getObjSpace().getTrue();
	}
	
}
