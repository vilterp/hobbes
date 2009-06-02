package hobbes.values;

import hobbes.interpreter.ObjectSpace;
import hobbes.parser.SourceLocation;

@HobbesClass(name="ArgumentError")
public class HbArgumentError extends HbSyntaxError {

	public HbArgumentError(ObjectSpace o, String m, SourceLocation l) {
		super(o, m, l);
	}
	
	public HbArgumentError(ObjectSpace o, StringBuilder m, SourceLocation l) {
		super(o, m.toString(), l);
	}

}
