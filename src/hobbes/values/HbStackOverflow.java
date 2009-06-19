package hobbes.values;

import hobbes.interpreter.Interpreter;

public class HbStackOverflow extends HbError {

	public HbStackOverflow(Interpreter i) {
		super(i, null);
	}
	
}
