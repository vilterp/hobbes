package hobbes.interpreter;

import hobbes.parser.SourceLocation;

public class NativeMethodFrame extends ExecutionFrame {
	
	private String className;
	private String methodName;
	private SourceLocation callLoc;
	
	public NativeMethodFrame(Scope s, String cn, String mn, SourceLocation l) {
		super(s);
		className = cn;
		methodName = mn;
		callLoc = l;
	}
	
	public String show() {
		return "  in " + className + "#" + methodName + "\n"
				+ showLoc(callLoc);
	}

}
