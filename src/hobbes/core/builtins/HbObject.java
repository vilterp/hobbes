package hobbes.core.builtins;

import hobbes.core.ObjectSpace;

import java.util.HashMap;

public class HbObject {
	
	protected HashMap<String,Integer> instanceVars;
	protected HashMap<String,HbMethod> methods;
	protected ObjectSpace objSpace;
	protected int id;
	
	public HbObject(ObjectSpace o) {
		// insert self into object space
		objSpace = o;
		id = objSpace.add(this);
		// initialize stuff
		instanceVars = new HashMap<String,Integer>();
		methods = new HashMap<String,HbMethod>();
	}
	
	public HbInt id() {
		return new HbInt(objSpace,id);
	}
	
}
