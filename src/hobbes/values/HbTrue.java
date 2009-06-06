package hobbes.values;

import hobbes.interpreter.ObjectSpace;

@HobbesClass(name="TrueClass")
public class HbTrue extends HbObject {
	
	public HbTrue(ObjectSpace o) {
		super(o);
	}
	
	@HobbesMethod(name="toString")
	public HbString hbToString() {
		return new HbString(getObjSpace(),"true");
	}

}
