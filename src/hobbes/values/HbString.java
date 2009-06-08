package hobbes.values;

import java.util.regex.Pattern;

import hobbes.interpreter.Interpreter;

@HobbesClass(name="String")
public class HbString extends HbObject {
	
	private StringBuilder value;
	
	public HbString(Interpreter i) {
		super(i);
		value = new StringBuilder();
	}
	
	public HbString(Interpreter i, String val) {
		super(i);
		value = new StringBuilder(val);
	}
	
	public HbString(Interpreter i, StringBuilder val) {
		this(i,val.toString());
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
