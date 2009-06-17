package hobbes.values;

import hobbes.interpreter.Interpreter;

@HobbesClass(name="FalseClass")
public class HbFalse extends HbObject {
	
	public HbFalse(Interpreter o) {
		super(o);
	}
	
	@HobbesMethod(name="toBool")
	public HbObject toBool() {
		return this;
	}
	
	@HobbesMethod(name="toString")
	public HbString hbToString() {
		return getObjSpace().getString("false");
	}

}
