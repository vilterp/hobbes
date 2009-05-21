package hobbes.core;

import hobbes.core.builtins.HbObject;

public class ObjectRecord {
	
	private HbObject obj;
	private int numRefs;
	
	public ObjectRecord(HbObject o) {
		obj = o;
		numRefs = 0;
	}
	
	public void incRefs() {
		numRefs++;
	}
	
	public int getNumRefs() {
		return numRefs;
	}
	
	public HbObject getObj() {
		return obj;
	}
	
}
