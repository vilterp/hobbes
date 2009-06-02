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
	
	@HobbesMethod(name="show",numArgs=0)
	public HbString show() {
		return new HbString(getObjSpace(),"\"" + value.toString() + "\"");
	}

}
