package hobbes.values;

import hobbes.interpreter.Interpreter;

@HobbesClass(name="TypeError")
public class HbTypeError extends HbError {
	
	public HbTypeError(Interpreter o) throws HbArgumentError {
		super(o);
		throw getNoMessageError();
	}

	public HbTypeError(Interpreter o, String m) {
		super(o, m);
	}

}
