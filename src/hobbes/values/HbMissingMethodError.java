package hobbes.values;

import hobbes.interpreter.Interpreter;

@HobbesClass(name="MissingMethodError",superclass="Error")
public class HbMissingMethodError extends HbError {
	
	public HbMissingMethodError(Interpreter i) throws HbArgumentError {
		super(i);
		throw getNoMessageError();
	}
	
	public HbMissingMethodError(Interpreter i, String m, String c) {
		super(i, m + " (class " + c + ")");
	}

}
