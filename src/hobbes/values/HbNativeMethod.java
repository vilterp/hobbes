package hobbes.values;

import java.lang.reflect.Method;

import hobbes.ast.ExpressionNode;

public class HbNativeMethod extends HbMethod {
	
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
