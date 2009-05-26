package hobbes.values;

import hobbes.core.ObjectSpace;

public class HbFalse extends HbBoolean {

	public HbFalse(ObjectSpace o) {
		super(o);
	}
	
	public HbString getType() {
		return new HbString(getObjSpace(),"False");
	}
	
	
	public HbBoolean is(HbValue other) {
		if(other instanceof HbFalse)
			return getObjSpace().getTrue();
		else
			return getObjSpace().getFalse();
	}
	
	public HbString show() {
		return new HbString(getObjSpace(),"false");
	}

}