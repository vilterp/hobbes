package hobbes.values;

import hobbes.interpreter.ObjectSpace;

@HobbesClass(name="NormalObject")
public class HbNormalObject extends HbObject {
	
	public HbNormalObject(ObjectSpace o) {
		super(o);
		throw new IllegalArgumentException("HbNormalObject needs an HbClass");
	}
	
	public HbNormalObject(ObjectSpace o, HbClass klass) {
		super(o,klass);
	}
	
}
