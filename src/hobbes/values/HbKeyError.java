package hobbes.values;

import hobbes.interpreter.Interpreter;

@HobbesClass(name="KeyError")
public class HbKeyError extends HbError {
	
	public HbKeyError(Interpreter i) throws HbArgumentError {
		super(i);
		throw getNoMessageError();
	}
	
	public HbKeyError(Interpreter i, String m) {
		super(i, m);
	}

}
