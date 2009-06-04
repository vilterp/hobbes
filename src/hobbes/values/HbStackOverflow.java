package hobbes.values;

import hobbes.interpreter.ObjectSpace;
import hobbes.parser.SourceLocation;

public class HbStackOverflow extends HbError {

	public HbStackOverflow(ObjectSpace o) {
		super(o, null);
	}
	
}
