package hobbes.core;

import java.util.HashMap;

import hobbes.values.*;

public class ObjectSpace {

	private HashMap<Integer, HbValue> objects;
	private HashMap<Integer, HbInt> intConstants;
	private HashMap<Float, HbFloat> floatConstants;
	private int nextId;
	private int trueId;
	private int falseId;
	private int nilId;

	public ObjectSpace() {
		objects = new HashMap<Integer, HbValue>();
		intConstants = new HashMap<Integer, HbInt>();
		floatConstants = new HashMap<Float, HbFloat>();
		nextId = 0;
		trueId = new HbTrue(this).getId();
		falseId = new HbFalse(this).getId();
		nilId = new HbNil(this).getId();
	}

	private int getId() {
		int id = nextId;
		nextId++;
		return id;
	}

	public HbValue get(int id) {
		return objects.get(id);
	}

	public int add(HbValue val) {
		int id = getId();
		set(id, val);
		return id;
	}

	public void set(int id, HbValue val) {
		objects.put(id, val);
	}

	public HbBoolean getTrue() {
		return (HbBoolean) get(trueId);
	}

	public HbBoolean getFalse() {
		return (HbBoolean) get(falseId);
	}

	public HbNil getNil() {
		return (HbNil) get(nilId);
	}

	public HbInt getInt(Integer val) {
		if (intConstants.containsKey(val))
			return intConstants.get(val);
		else {
			HbInt newConstant = new HbInt(this,val);
			intConstants.put(val, newConstant);
			return newConstant;
		}
	}

	public HbFloat getFloat(Float val) {
		if (intConstants.containsKey(val))
			return floatConstants.get(val);
		else {
			HbFloat newConstant = new HbFloat(this,val);
			floatConstants.put(val, newConstant);
			return newConstant;
		}
	}

}
