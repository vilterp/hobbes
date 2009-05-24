package hobbes.core.builtins;

import hobbes.core.ObjectSpace;
import hobbes.parser.SourceLocation;

public class HbSyntaxError extends HbError {
	
	public HbSyntaxError(ObjectSpace o, SourceLocation l, String msg) {
		super(o, l, msg);
	}
	
	public String getName() {
		return "SyntaxError";
	}

}
