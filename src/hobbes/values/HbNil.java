package hobbes.values;

import hobbes.interpreter.ObjectSpace;

@HobbesClass(name="NilClass")
public class HbNil extends HbObject {

	public HbNil(ObjectSpace o) {
		super(o);
	}

	@HobbesMethod(name="show",numArgs=0)
	public HbString show() {
		return new HbString(getObjSpace(),"nil");
	}

}
