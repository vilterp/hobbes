package hobbes.core.builtins;

import hobbes.core.NativeMethod;
import hobbes.core.ObjectSpace;

public class HbInt extends HbObject {
	
	private int value;
	
	public HbInt(ObjectSpace objSpace, int val) {
		super(objSpace);
		value = val;
	}
	
	public String toString() {
		return new Integer(value).toString();
	}
	
	public String getName() {
		return "Int";
	}
	
	public int getValue() {
		return value;
	}
	
	@NativeMethod(name="+",numArgs=1)
	public HbInt plus(HbInt other) {
		return new HbInt(objSpace,value + other.getValue());
	}
	
}
