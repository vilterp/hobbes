package hobbes.values;

import hobbes.interpreter.ObjectSpace;

public class HbNil extends HbObject {

	public HbNil(ObjectSpace o) {
		super(o);
	}
	
	@Override
	public HbString show() {
		return new HbString(getObjSpace(),"nil");
	}

}
