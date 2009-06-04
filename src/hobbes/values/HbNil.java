package hobbes.values;

import hobbes.interpreter.ObjectSpace;

@HobbesClass(name="NilClass")
public class HbNil extends HbObject {

	public HbNil(ObjectSpace o) {
		super(o);
	}
	
	public String toString() {
		return "nil";
	}

}
