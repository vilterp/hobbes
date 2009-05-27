package hobbes.values;

import hobbes.core.ObjectSpace;

public class HbString extends HbValue {
	
	private StringBuilder value;
	
	public HbString(ObjectSpace o, String val) {
		super(o);
		value = new StringBuilder(val);
	}
	
	public String toString() {
		return value.toString();
	}
	
	public String sanitizedValue() {
		return getValue().toString()
				.replaceAll("\n", "\\\\n")
				.replaceAll("\t", "\\\\t")
				.replaceAll("\"", "\\\"");
	}
	
	public String getValue() {
		return value.toString();
	}
	
	public HbString getType() {
		return new HbString(getObjSpace(),"String");
	}
	
	public HbBoolean is(HbValue other) {
		if(other instanceof HbString) {
			if(((HbString)other).getValue().equals(getValue()))
				return getObjSpace().getTrue();
			else
				return getObjSpace().getFalse();
		} else
			return getObjSpace().getFalse();
	}
	
	public HbString show() {
		return new HbString(getObjSpace(),"\"" + sanitizedValue() + "\"");
	}

}
