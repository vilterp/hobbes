package hobbes.values;

import hobbes.interpreter.ObjectSpace;
import hobbes.parser.SourceLocation;

@HobbesClass(name="NotAClassError")
public class HbNotAClassError extends HbError {

	public HbNotAClassError(ObjectSpace o, String m) {
		super(o, m);
	}

}
