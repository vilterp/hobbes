package hobbes.values;

import hobbes.interpreter.ObjectSpace;
import hobbes.parser.SourceLocation;

@HobbesClass(name="ArgumentError")
public class HbArgumentError extends HbError {
	
	public HbArgumentError(ObjectSpace o) throws HbArgumentError {
		super(o);
		throw getNoMessageError();
	}
	
	public HbArgumentError(ObjectSpace o, String m) {
		super(o, m);
	}
	
	public HbArgumentError(ObjectSpace o, StringBuilder m) {
		super(o, m.toString());
	}
	
	public HbArgumentError(ObjectSpace o, String methodName, String gotten, String expected) {
		super(o,methodName + " got a(n)" + gotten + ", but expected a " + expected + ".");
	}

}
