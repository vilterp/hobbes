package hobbes.values;

import java.lang.reflect.Method;

public class HbNativeMethod implements HbMethod {
	
	private String name;
	private int numArgs;
	private Method method;
	
	public HbNativeMethod(String n, int na, Method m) {
		name = n;
		numArgs = na;
		method = m;
	}
	
	public String getName() {
		return name;
	}
	
	public int getNumArgs() {
		return numArgs;
	}
	
	public Method getMethod() {
		return method;
	}
	
}
