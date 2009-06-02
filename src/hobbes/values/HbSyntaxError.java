package hobbes.values;

import hobbes.interpreter.ObjectSpace;
import hobbes.parser.SourceLocation;

@HobbesClass(name="SyntaxError")
public class HbSyntaxError extends HbError {

	public HbSyntaxError(ObjectSpace o, String m, SourceLocation l) {
		super(o, m, l);
	}

}