package hobbes.interpreter;

import hobbes.parser.SourceLocation;

public class FunctionFrame extends ExecutionFrame {
	
	public String name;
	public SourceLocation loc;
	
	public FunctionFrame(ObjectSpace o, Scope adoptGlobals,
						String na, SourceLocation p) {
		super(new Scope(o,adoptGlobals));
		name = na;
		loc = p;
	}
	
	public SourceLocation getLoc() {
		return loc;
	}
	
	public String show() {
		return "  in " + name + "\n"
				+ loc.show();
	}
	
}
