package hobbes.values;

import hobbes.interpreter.Interpreter;

@HobbesClass(name="TrueClass")
public class HbTrue extends HbObject {
	
	public HbTrue(Interpreter o) {
		super(o);
	}
	
	@HobbesMethod(name="show")
	public HbString hbShow() {
		return getObjSpace().getString("true");
	}
	
	@HobbesMethod(name="toString")
	public HbString hbToString() {
		return hbShow();
	}

}
