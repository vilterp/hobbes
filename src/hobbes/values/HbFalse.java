package hobbes.values;

import hobbes.interpreter.ObjectSpace;

@HobbesClass(name="FalseClass")
public class HbFalse extends HbObject {
	
	public HbFalse(ObjectSpace o) {
		super(o);
	}

	@HobbesMethod(name="show",numArgs=0)
	public HbString show() {
		return new HbString(getObjSpace(),"false");
	}

}
