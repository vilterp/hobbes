package hobbes.values;

public class HbNativeMethod implements HbMethod {
	
	private String name;
	
	public HbNativeMethod(String n) {
		name = n;
	}
	
	public String getName() {
		return name;
	}
	
}
