package hobbes.interpreter;

import hobbes.values.HbInstance;

public class ValueRecord {
	
	private HbInstance value;
	private int numRefs;
	
	public ValueRecord(HbInstance val) {
		value = val;
		numRefs = 0;
	}
	
	public void incRefs() {
		numRefs++;
	}
	
	public HbInstance getValue() {
		return value;
	}
	
	public boolean isReferenced() {
		return numRefs > 0;
	}
	
}
