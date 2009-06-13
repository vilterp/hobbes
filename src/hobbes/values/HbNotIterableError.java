package hobbes.values;

import hobbes.interpreter.Interpreter;

@HobbesClass(name="NotIterableError")
public class HbNotIterableError extends HbError {
	
	public HbNotIterableError(Interpreter i) {
		super(i);
	}
	
	public HbNotIterableError(Interpreter i, String m) {
		super(i,m);
	}
	
}
