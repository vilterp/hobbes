package hobbes.core.builtins;

import hobbes.core.ObjectSpace;
import hobbes.core.NativeMethod;

import java.util.HashMap;

public class HbObject extends Throwable {
	
	protected HashMap<String,Integer> instanceVars;
	protected HashMap<String,Integer> methods;
	protected ObjectSpace objSpace;
	protected int id;
	
	public HbObject(ObjectSpace o) {
		// insert self into object space
		objSpace = o;
		id = objSpace.add(this);
		// initialize stuff
		instanceVars = new HashMap<String,Integer>();
		methods = new HashMap<String,Integer>();
	}
	
	public String getName() {
		return "Object";
	}
	
	@NativeMethod(name="id",numArgs=0)
	public HbInt id() {
		return new HbInt(objSpace,id);
	}
	
}
