package hobbes.values;

import hobbes.interpreter.ObjectSpace;

@HobbesClass(name="Int")
public class HbInt extends HbObject {
	
	private int value;
	
	public HbInt(ObjectSpace o, int val) {
		super(o);
		value = val;
	}
	
	public int getValue() {
		return value;
	}
	
	public String toString() {
		return new Integer(value).toString();
	}
	
}
