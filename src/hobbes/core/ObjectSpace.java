package hobbes.core;

import hobbes.core.builtins.HbObject;

import java.util.HashMap;

public class ObjectSpace {
	
	private HashMap<String,Integer> variables;
	private HashMap<Integer,ObjectRecord> objects;
	private int latestId;
	
	public ObjectSpace() {
		variables = new HashMap<String,Integer>();
		objects = new HashMap<Integer,ObjectRecord>();
		latestId = 0;
	}
	
	public HbObject get(String varName) {
		return objects.get(variables.get(varName)).getObj();
	}
	
	public HbObject get(int id) {
		return objects.get(id).getObj();
	}
	
	public void set(String varName, int objId) {
		if(!variables.containsKey(varName) || variables.get(varName) != objId) {
			variables.put(varName, objId);
			objects.get(objId).incRefs();
		}
	}
	
	public void set(int id, HbObject obj) {
		objects.put(id, new ObjectRecord(obj));
	}
	
	public int add(HbObject obj) {
		int id = nextId();
		set(id, obj);
		return id;
	}
	
	public int nextId() {
		int temp = latestId;
		latestId++;
		return temp;
	}
	
	public void garbageCollect(int id) {
		if(objects.containsKey(id) && objects.get(id).getNumRefs() == 0)
			objects.remove(id);
	}
	
}
