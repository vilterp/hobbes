package hobbes.interpreter;

import hobbes.parser.SourceLocation;

public class NativeMethodFrame extends MethodFrame {
	
	private String className;
	private String methodName;
	private SourceLocation callLoc;
	
	public NativeMethodFrame(Scope s, String cn, String mn, SourceLocation l) {
		super(s);
		className = cn;
		methodName = mn;
		callLoc = l;
	}
	
	public String getName() {
		return className + "#" + methodName;
	}
	
	public String show() {
		return "  in " + getName() + "\n"
				+ showLoc(callLoc);
	}

}
