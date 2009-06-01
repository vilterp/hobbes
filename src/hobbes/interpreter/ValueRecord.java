package hobbes.interpreter;

import hobbes.values.HbObject;

public class ValueRecord {
	
	private HbObject value;
	private int numRefs;
	
	public ValueRecord(HbObject val) {
		value = val;
		numRefs = 0;
	}
	
	public void incRefs() {
		numRefs++;
	}
	
	public HbObject getValue() {
		return value;
	}
	
	public boolean isReferenced() {
		return numRefs > 0;
	}
	
}
