package hobbes.values;

import hobbes.interpreter.ObjectSpace;
import hobbes.parser.SourceLocation;

@HobbesClass(name="UndefinedNameError")
public class HbUndefinedNameError extends HbError {

	public HbUndefinedNameError(ObjectSpace o, String m, SourceLocation l) {
		super(o, m, l);
	}

}
