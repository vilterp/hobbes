package hobbes.interpreter;

import hobbes.parser.SourceLocation;

public class NativeFunctionFrame extends FunctionFrame {
	
	private String name;
	private SourceLocation callLoc;
	
	public NativeFunctionFrame(Scope s, String n, SourceLocation l) {
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
