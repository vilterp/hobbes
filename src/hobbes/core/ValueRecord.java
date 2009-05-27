package hobbes.core;

import hobbes.values.HbValue;

public class ValueRecord {
	
	private HbValue value;
	private int numRefs;
	
	public ValueRecord(HbValue val) {
		value = val;
		numRefs = 0;
	}
	
	public void incRefs() {
		numRefs++;
	}
	
	public HbValue getValue() {
		return value;
	}
	
	public boolean isReferenced() {
		return numRefs > 0;
	}
	
}
