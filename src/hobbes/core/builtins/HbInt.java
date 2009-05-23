package hobbes.core.builtins;

import hobbes.core.ObjectSpace;

public class HbInt extends HbObject {
	
	private int value;
	
	public HbInt(ObjectSpace objSpace, int val) {
		super(objSpace);
		value = val;
	}
	
}
