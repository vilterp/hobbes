package hobbes.values;

import java.util.ArrayList;

import hobbes.interpreter.ExecutionFrame;
import hobbes.interpreter.ObjectSpace;
import hobbes.parser.SourceLocation;

@HobbesClass(name="Error")
public class HbError extends HbObject {
	
	private String errorMessage;
	private SourceLocation location;
	private ArrayList<ExecutionFrame> trace;
	
	public HbError(ObjectSpace o, String m, SourceLocation l) {
		super(o);
		errorMessage = m;
		location = l;
		trace = new ArrayList<ExecutionFrame>();
	}
	
	public void addFrame(ExecutionFrame f) {
		trace.add(f);
	}
	
	public void printStackTrace() {
		System.err.print(getClassInstance().getName());
		if(getMessage() != null)
			System.err.print(": " + getMessage());
		System.err.println();
		System.err.println(location.show());
		for(ExecutionFrame f: trace)
			System.err.println(f.show());
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
