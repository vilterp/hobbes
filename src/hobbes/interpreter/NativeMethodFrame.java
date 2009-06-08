package hobbes.interpreter;

import hobbes.parser.SourceLocation;

public class NativeMethodFrame extends ExecutionFrame {
	
	private String className;
	private String methodName;
	private SourceLocation loc;
	
	public NativeMethodFrame(String cn, String mn, SourceLocation l) {
		super(null);
		className = cn;
		methodName = mn;
		loc = l;
	}
	
	public String show() {
		return "  in " + className + "#" + methodName + "\n"
		+ loc.show();
	}

}
