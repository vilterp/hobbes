package hobbes.values;

import hobbes.interpreter.Interpreter;

@HobbesClass(name="NilClass")
public class HbNil extends HbObject {

	public HbNil(Interpreter o) {
		super(o);
	}
	
	@HobbesMethod(name="toString")
	public HbString hbToString() {
		return new HbString(getInterp(),"nil");
	}

}
