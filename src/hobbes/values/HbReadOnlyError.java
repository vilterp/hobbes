package hobbes.values;

import hobbes.interpreter.ObjectSpace;
import hobbes.parser.SourceLocation;

@HobbesClass(name="ReadOnlyError")
public class HbReadOnlyError extends HbError {

	public HbReadOnlyError(ObjectSpace o, String m, SourceLocation l) {
		super(o, m, l);
	}

}
