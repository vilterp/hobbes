package hobbes.interpreter;

import java.util.HashMap;

import hobbes.values.*;

public class ObjectSpace {

	private HashMap<Integer,ValueRecord> objects;
	private HashMap<Integer,HbInt> intConstants;
	//private HashMap<Float, HbFloat> floatConstants;
	private HashMap<String,HbObject> builtins;
	private int nextId;

	public ObjectSpace() {
		objects = new HashMap<Integer,ValueRecord>();
		builtins = new HashMap<String,HbObject>();
		intConstants = new HashMap<Integer,HbInt>();
		//floatConstants = new HashMap<Float, HbFloat>();
		nextId = 0;
		// add builtin classes
		HbClass metaClass = new HbClass(this);
		builtins.put("Class",metaClass);
		addBuiltinClass("Object");
		addBuiltinClass("TrueClass");
		addBuiltinClass("FalseClass");
		addBuiltinClass("NilClass");
		addBuiltinClass("Int");
		addBuiltinClass("Float");
		addBuiltinClass("String");
		// add builtin globals
		builtins.put("nil",new HbNil(this));
		builtins.put("true",new HbTrue(this));
		builtins.put("false",new HbFalse(this));
	}
	
	private void addBuiltinClass(String name) {
		builtins.put(name, new HbClass(this,name));
	}
	
	public HashMap<String,HbObject> getBuiltins() {
		return builtins;
	}
	
	public HbClass getBuiltinClass(String name) {
		return (HbClass)builtins.get(name);
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
		return builtins.get("true");
	}

	public HbObject getFalse() {
		return builtins.get("false");
	}

	public HbObject getNil() {
		return builtins.get("nil");
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
