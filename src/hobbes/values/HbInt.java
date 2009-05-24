package hobbes.values;

public class HbInt implements HbValue {
	
	private int value;
	
	public HbInt(int v) {
		value = v;
	}
	
	public String show() {
		return new Integer(value).toString();
	}
	
	public int getValue() {
		return value;
	}

}
