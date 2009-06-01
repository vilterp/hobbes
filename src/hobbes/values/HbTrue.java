package hobbes.values;

import hobbes.interpreter.ObjectSpace;

public class HbTrue extends HbObject {
	
	public HbTrue(ObjectSpace o) {
		super(o);
	}
	
	@Override
	public HbString show() {
		return new HbString(getObjSpace(),"true");
	}

}
