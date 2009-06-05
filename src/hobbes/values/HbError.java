package hobbes.values;

import java.util.ArrayList;

import hobbes.interpreter.ExecutionFrame;
import hobbes.interpreter.ObjectSpace;
import hobbes.parser.SourceLocation;

@HobbesClass(name="Error")
public class HbError extends HbObject {
	
	private String errorMessage;
	
	public HbError(ObjectSpace o) {
		super(o);
		errorMessage = null;
	}
	
	public HbError(ObjectSpace o, String m) {
		super(o);
		errorMessage = m;
	}
	
	protected HbArgumentError getNoMessageError() {
		return new HbArgumentError(getObjSpace(),
				getHbClass().getName() + " needs a message");
	}

	public String getMessage() {
		return errorMessage;
	}
	
	public String toString() {
		StringBuilder ans = new StringBuilder("<");
		ans.append(getHbClass().getName());
		if(getMessage() != null) {
			ans.append(": \"");
			ans.append(getMessage());
			ans.append("\"");
		}
		ans.append(">");
		return ans.toString();
	}
	
}
