package hobbes.values;

import hobbes.interpreter.Interpreter;

@HobbesClass(name="Int")
public class HbInt extends HbObject {
	
	private int value;
	
	public HbInt(Interpreter o) throws HbArgumentError {
		super(o);
		throw new HbArgumentError(getInterp(),
				"Can't make an Int with no parameters");
	}
	
	public HbInt(Interpreter o, int val) {
		super(o);
		value = val;
	}
	
	public int getValue() {
		return value;
	}
	
	@HobbesMethod(name="toString")
	public HbString hbToString() {
		return new HbString(getInterp(),new Integer(value).toString());
	}
	
}
