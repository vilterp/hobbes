package hobbes.core;

import hobbes.parser.SourceLocation;

public class FunctionFrame extends ExecutionFrame implements ShowableFrame {
	
	public String name;
	public SourceLocation loc;
	public boolean isNative;
	
	public FunctionFrame(ExecutionFrame e, ObjectSpace o,
							String na, SourceLocation p, boolean in) {
		super(e,o);
		name = na;
		loc = p;
		isNative = in;
	}
	
	public SourceLocation getLoc() {
		return loc;
	}
	
	public boolean isNative() {
		return isNative;
	}
	
	public String show() {
		return "  in " + name
				+ "(" + loc.getLine().getFileName()
				+ ":" + loc.getLine().getLineNo() + ")\n"
				+ loc.show();
	}
	
}
