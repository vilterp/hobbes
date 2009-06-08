package hobbes.values;

import hobbes.interpreter.Interpreter;

@HobbesClass(name="KeyError")
public class HbKeyError extends HbError {
	
	public HbKeyError(Interpreter o) throws HbArgumentError {
		super(o);
		throw getNoMessageError();
	}
	
	public HbKeyError(Interpreter o, String m) {
		super(o, m);
	}

}
