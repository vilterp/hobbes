package hobbes.interpreter;

import hobbes.parser.SourceLocation;

public class MethodFrame extends ExecutionFrame {
	
	public String name;
	public String className;
	public SourceLocation loc;
	
	public MethodFrame(ObjectSpace o, Scope adoptGlobals,
						String na, String cn, SourceLocation p) {
		super(new Scope(o,adoptGlobals));
		name = na;
		className = cn;
		loc = p;
	}
	
	public SourceLocation getLoc() {
		return loc;
	}
	
	public String show() {
		return "  in " + className + "#" + name + "\n"
				+ loc.show();
	}
	
}
