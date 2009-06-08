package hobbes.values;

import hobbes.interpreter.Interpreter;

@HobbesClass(name="SyntaxError")
public class HbSyntaxError extends HbError {
	
	public HbSyntaxError(Interpreter o) throws HbArgumentError {
		super(o);
		throw getNoMessageError();
	}
	
	public HbSyntaxError(Interpreter o, String m) {
		super(o, m);
	}

}
