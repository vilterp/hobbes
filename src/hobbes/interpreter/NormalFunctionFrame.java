package hobbes.interpreter;

import hobbes.parser.SourceLocation;

public class NormalFunctionFrame extends FunctionFrame {
	
	private String name;
	private SourceLocation callLoc;
	
	public NormalFunctionFrame(Scope s, String n, SourceLocation l) {
		super(s);
		name = n;
		callLoc = l;
	}
	
	public String getName() {
		return name;
	}
	
	public String show() {
		return "  in " + name + "\n"
				+ showLoc(callLoc);
	}

}
