package hobbes.values;

import hobbes.interpreter.ObjectSpace;
import hobbes.parser.SourceLocation;

@HobbesClass(name="MissingMethodError")
public class HbMissingMethodError extends HbError {

	public HbMissingMethodError(ObjectSpace o, String m) {
		super(o, m);
	}

}
