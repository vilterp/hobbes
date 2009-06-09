package hobbes.interpreter;

import hobbes.parser.SourceLocation;

public class FunctionFrame extends ExecutionFrame {
	
	private String name;
	private SourceLocation callLoc;
	
	public FunctionFrame(Scope s, String n, SourceLocation l) {
		super(s);
		name = n;
		callLoc = l;
	}
	
	public String show() {
		return "  in " + name + "\n"
				+ showLoc(callLoc);
	}

}
