package hobbes.values;

import hobbes.interpreter.ObjectSpace;

public class HbNil extends HbValue {
	
	public HbNil(ObjectSpace o) {
		super(o);
	}
	
	public HbString getType() {
		return new HbString(getObjSpace(),"Nil");
	}
	
	public HbBoolean is(HbValue other) {
		if(other instanceof HbNil)
			return getObjSpace().getTrue();
		else
			return getObjSpace().getFalse();
	}
	
	public HbString show() {
		return new HbString(getObjSpace(),"nil");
	}
	
	public HbBoolean toBool() {
		return getObjSpace().getFalse();
	}
	
}
