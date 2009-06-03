package hobbes.interpreter;

import java.util.HashMap;

import hobbes.values.*;

public class ObjectSpace {

	private HashMap<Integer,ValueRecord> objects;
	private HashMap<Integer,HbInt> intConstants;
	//private HashMap<Float, HbFloat> floatConstants;
	private HashMap<String,HbClass> classes;
	private int nextId;
	private int trueId;
	private int falseId;
	private int nilId;

	public ObjectSpace() {
		objects = new HashMap<Integer,ValueRecord>();
		classes = new HashMap<String,HbClass>();
		intConstants = new HashMap<Integer,HbInt>();
		//floatConstants = new HashMap<Float, HbFloat>();
		nextId = 0;
		// add builtin classes
		addClass(HbClass.class);
		addClass(HbObject.class);
		addClass(HbTrue.class);
		addClass(HbFalse.class);
		addClass(HbNil.class);
		addClass(HbInt.class);
//		addClass(HbFloat.class);
		addClass(HbString.class);
		addClass(HbError.class);
		addClass(HbSyntaxError.class);
		addClass(HbMissingMethodError.class);
		addClass(HbUndefinedNameError.class);
		addClass(HbArgumentError.class);
		addClass(HbReadOnlyError.class);
		// add builtin globals
		nilId = new HbNil(this).getId();
		trueId = new HbTrue(this).getId();
		falseId = new HbFalse(this).getId();
	}
	
	private void addClass(Class<? extends HbObject> klass) {
		if(klass.isAnnotationPresent(HobbesClass.class)) {
			String name = ((HobbesClass)klass.getAnnotation(HobbesClass.class)).name();
			classes.put(name,new HbClass(this,klass));
			classes.get(name).setClass(classes.get("Class"));
		} else
			throw new IllegalArgumentException("Supplied class has no " +
													"HobbesClass annotation");
	}
	
	public HashMap<String,HbClass> getClasses() {
		return classes;
	}
	
	public HbClass getClass(String name) {
		return (HbClass)classes.get(name);
	}

	private int getId() {
		int id = nextId;
		nextId++;
		return id;
	}

	public HbObject get(int id) {
		return objects.get(id).getValue();
	}

	public int add(HbObject val) {
		int id = getId();
		set(id, val);
		return id;
	}

	public void set(int id, HbObject val) {
		objects.put(id, new ValueRecord(val));
	}
	
	public HbObject getBool(boolean condition) {
		if(condition)
			return getTrue();
		else
			return getFalse();
	}

	public HbObject getTrue() {
		return get(trueId);
	}

	public HbObject getFalse() {
		return get(falseId);
	}

	public HbObject getNil() {
		return get(nilId);
	}
	
	public HbObject nilIfNull(HbObject obj) {
		if(obj == null)
			return getNil();
		else
			return obj;
	}

	public HbInt getInt(Integer val) {
		if(intConstants.containsKey(val))
			return intConstants.get(val);
		else {
			HbInt newConstant = new HbInt(this,val);
			intConstants.put(val,newConstant);
			return newConstant;
		}
	}

//	public HbFloat getFloat(Float val) {
//		if (intConstants.containsKey(val))
//			return floatConstants.get(val);
//		else {
//			HbFloat newConstant = new HbFloat(this,val);
//			floatConstants.put(val, newConstant);
//			return newConstant;
//		}
//	}
	
	public void garbageCollect(int id) {
		if(!objects.get(id).isReferenced())
			objects.remove(id);
	}

}
