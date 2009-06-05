package hobbes.values;

import java.util.ArrayList;

import hobbes.interpreter.ExecutionFrame;
import hobbes.interpreter.ObjectSpace;
import hobbes.parser.SourceLocation;

@HobbesClass(name="Error")
public class HbError extends HbObject {
	
	private String errorMessage;
	
	public HbError(ObjectSpace o, String m) {
		super(o);
		errorMessage = m;
	}

	public String getMessage() {
		return errorMessage;
	}
	
	public String toString() {
		StringBuilder ans = new StringBuilder("<");
		ans.append(getClassInstance().getName());
		if(getMessage() != null) {
			ans.append(" ");
			ans.append("msg=");
			ans.append("\"");
			ans.append(getMessage());
			ans.append("\"");
		}
		ans.append(">");
		return ans.toString();
	}
	
}
