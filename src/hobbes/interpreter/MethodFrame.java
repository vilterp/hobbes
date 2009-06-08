package hobbes.interpreter;

import hobbes.parser.SourceLocation;
import hobbes.values.HbObject;

public class MethodFrame extends ExecutionFrame {
	
	private String methodName;
	private String className;
	private SourceLocation loc;
	private HbObject receiver;
	
	public MethodFrame(Interpreter i, Scope adoptGlobals, HbObject rec,
						String mn, SourceLocation p) {
		super(new Scope(i,adoptGlobals));
		methodName = mn;
		loc = p;
		receiver = rec;
		className = rec.getHbClass().getName();
	}
	
	public HbObject getReceiver() {
		return receiver;
	}
	
	public SourceLocation getLoc() {
		return loc;
	}
	
	public String show() {
		return "  in " + className + "#" + methodName + "\n"
				+ loc.show();
	}
	
}
