package hobbes.values;

import hobbes.interpreter.Interpreter;
import hobbes.interpreter.ObjectSpace;
import hobbes.parser.SourceLocation;

@HobbesClass(name="ArgumentError")
public class HbArgumentError extends HbError {
	
	public HbArgumentError(Interpreter i) throws HbArgumentError {
		super(i);
		throw getNoMessageError();
	}
	
	public HbArgumentError(Interpreter o, String m) {
		super(o, m);
	}
	
	public HbArgumentError(Interpreter o, StringBuilder m) {
		super(o, m.toString());
	}
	
	public HbArgumentError(Interpreter i, String methodName, HbObject gotten, String expected) {
		super(i,methodName + " got a(n) " + gotten.getHbClass().getName()
									+ ", but expected a " + expected + ".");
	}

}
