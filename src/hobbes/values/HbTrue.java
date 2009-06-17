package hobbes.values;

import hobbes.interpreter.Interpreter;

@HobbesClass(name="TrueClass")
public class HbTrue extends HbObject {
	
	public HbTrue(Interpreter o) {
		super(o);
	}
	
	@HobbesMethod(name="toString")
	public HbString hbToString() {
		return getObjSpace().getString("true");
	}

}
