package hobbes.values;

import hobbes.interpreter.Interpreter;

@HobbesClass(name="MissingMethodError")
public class HbMissingMethodError extends HbError {
	
	public HbMissingMethodError(Interpreter i) throws HbArgumentError {
		super(i);
		throw getNoMessageError();
	}
	
	public HbMissingMethodError(Interpreter i, String m) {
		super(i, m);
	}

}
