package hobbes.values;

import hobbes.interpreter.Interpreter;

@HobbesClass(name="UndefinedNameError")
public class HbUndefinedNameError extends HbError {
	
	public HbUndefinedNameError(Interpreter i) throws HbArgumentError {
		super(i);
		throw getNoMessageError();
	}
	
	public HbUndefinedNameError(Interpreter i, String m) {
		super(i, m);
	}

}
