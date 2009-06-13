package hobbes.values;

import hobbes.interpreter.Interpreter;

public abstract class HbNamedFunction extends HbFunction {

	public HbNamedFunction(Interpreter i) {
		super(i);
	}
	
	public abstract String getName();

}
