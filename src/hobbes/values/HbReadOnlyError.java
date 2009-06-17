package hobbes.values;

import hobbes.interpreter.Interpreter;

@HobbesClass(name="ReadOnlyError",superclass="Error")
public class HbReadOnlyError extends HbError {
	
	public HbReadOnlyError(Interpreter o) throws HbArgumentError {
		super(o);
		throw getNoMessageError();
	}

	public HbReadOnlyError(Interpreter o, String m) {
		super(o, m);
	}

}
