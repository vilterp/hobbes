package hobbes.values;

import hobbes.interpreter.ObjectSpace;

@HobbesClass(name="TrueClass")
public class HbTrue extends HbObject {
	
	public HbTrue(ObjectSpace o) {
		super(o);
	}

	@HobbesMethod(name="show",numArgs=0)
	public HbString show() {
		return new HbString(getObjSpace(),"true");
	}

}
