package hobbes.values;

import hobbes.interpreter.Interpreter;

@HobbesClass(name="NilClass")
public class HbNil extends HbObject {

	public HbNil(Interpreter o) {
		super(o);
	}
	
	@HobbesMethod(name="toBool")
	public HbObject toBool() {
		return getObjSpace().getFalse();
	}
	
	@HobbesMethod(name="show")
	public HbString hbShow() {
		return getObjSpace().getString("nil");
	}
	
	@HobbesMethod(name="toString")
	public HbString hbToString() {
		return hbShow();
	}

}
