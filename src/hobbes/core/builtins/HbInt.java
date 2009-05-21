package hobbes.core.builtins;

import hobbes.core.ObjectSpace;

public class HbInt extends HbObject {

	public HbInt(ObjectSpace objSpace, int value) {
		super(objSpace);
		instanceVars.put("value", value);
	}

}
