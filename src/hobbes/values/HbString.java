package hobbes.values;

import hobbes.interpreter.ObjectSpace;

@HobbesClass(name="String")
public class HbString extends HbObject {
	
	private StringBuilder value;
	
	public HbString(ObjectSpace o, String val) {
		super(o);
		value = new StringBuilder(val);
	}
	
	public HbString(ObjectSpace o, StringBuilder val) {
		this(o,val.toString());
	}
	
	public StringBuilder getValue() {
		return value;
	}
	
	public String toString() {
		return "\"" + value.toString() + "\"";
	}

}
