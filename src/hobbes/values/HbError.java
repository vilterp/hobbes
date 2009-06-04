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

	@HobbesMethod(name="show",numArgs=0)
	public HbString show() {
		StringBuilder ans = new StringBuilder("<Error");
		if(getMessage() != null) {
			ans.append(" ");
			ans.append("msg=");
			ans.append("\"");
			ans.append(getMessage());
			ans.append("\"");
		}
		ans.append(">");
		return new HbString(getObjSpace(),ans);
	}
	
}
