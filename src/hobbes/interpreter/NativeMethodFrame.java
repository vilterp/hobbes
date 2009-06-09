package hobbes.interpreter;

import hobbes.parser.SourceLocation;

public class NativeMethodFrame extends ExecutionFrame {
	
	private String className;
	private String methodName;
	private SourceLocation callLoc;
	
	public NativeMethodFrame(String cn, String mn, SourceLocation l) {
		super(null);
		className = cn;
		methodName = mn;
		callLoc = l;
	}
	
	public String show() {
		return "  in " + className + "#" + methodName + "\n"
				+ showLoc(callLoc);
	}

}
