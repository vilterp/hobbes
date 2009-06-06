package hobbes.values;

import java.util.regex.Pattern;

import hobbes.interpreter.ObjectSpace;

@HobbesClass(name="String")
public class HbString extends HbObject {
	
	private StringBuilder value;
	
	public HbString(ObjectSpace o) {
		super(o);
		value = new StringBuilder();
	}
	
	public HbString(ObjectSpace o, String val) {
		super(o);
		value = new StringBuilder(val);
	}
	
	public HbString(ObjectSpace o, StringBuilder val) {
		this(o,val.toString());
	}
	
	public String sanitizedValue() {
		// backslash craziness!
		return value.toString()
				.replaceAll("\n", "\\\\n")
				.replaceAll("\t", "\\\\t")
				.replaceAll("\"", "\\\\\"");
	}
	
	public String getValue() {
		return value.toString();
	}
	
	@HobbesMethod(name="toString")
	public HbString hbToString() {
		return this;
	}
	
	public int hashCode() {
		return value.toString().hashCode();
	}
	
	@HobbesMethod(name="length")
	public HbInt length() {
		return getObjSpace().getInt(value.length());
	}

}
