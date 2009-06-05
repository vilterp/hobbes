package hobbes.values;

import hobbes.interpreter.ObjectSpace;

@HobbesClass(name="Int")
public class HbInt extends HbObject {
	
	private int value;
	
	public HbInt(ObjectSpace o) throws HbArgumentError {
		super(o);
		throw new HbArgumentError(getObjSpace(),
				"Can't make an Int with no parameters");
	}
	
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
