package hobbes.interpreter;

import hobbes.parser.SourceLocation;

public class FunctionFrame extends ExecutionFrame implements ShowableFrame {
	
	public String name;
	public SourceLocation loc;
	public boolean isNative;
	
	public FunctionFrame(ObjectSpace o, Scope adoptGlobals,
						String na, SourceLocation p, boolean in) {
		super(new Scope(o,adoptGlobals));
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
		return "  in " + name + "\n"
				+ loc.show();
	}
	
}
