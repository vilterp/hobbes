package hobbes.values;

import hobbes.interpreter.Interpreter;

@HobbesClass(name="NotAClassError",superclass="Error")
public class HbNotAClassError extends HbError {
	
	public HbNotAClassError(Interpreter o) throws HbArgumentError {
		super(o);
		throw getNoMessageError();
	}

	public HbNotAClassError(Interpreter o, String m) {
		super(o, m);
	}

}
