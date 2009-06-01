package hobbes.values;

import hobbes.interpreter.ObjectSpace;

public class HbFalse extends HbObject {
	
	public HbFalse(ObjectSpace o) {
		super(o);
	}
	
	@Override
	public HbString show() {
		return new HbString(getObjSpace(),"false");
	}

}
