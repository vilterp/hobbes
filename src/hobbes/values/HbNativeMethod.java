package hobbes.values;

import java.lang.reflect.Method;

import hobbes.ast.ExpressionNode;

public class HbNativeMethod extends HbMethod {
	
	private String name;
	private String className;
	private int numArgs;
	private Method method;
	
	public HbNativeMethod(String n, String cn, int na, Method m) {
		name = n;
		className = cn;
		numArgs = na;
		method = m;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDeclaringClassName() {
		return className;
	}
	
	public int getNumArgs() {
		return numArgs;
	}
	
	public Method getMethod() {
		return method;
	}
	
}
