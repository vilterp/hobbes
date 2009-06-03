package hobbes.interpreter;

import hobbes.parser.SourceLocation;
import hobbes.values.HbError;

public class HbStackOverflow extends HbError {

	public HbStackOverflow(ObjectSpace o, SourceLocation l) {
		super(o, null, l);
	}
	
}
