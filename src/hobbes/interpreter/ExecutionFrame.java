package hobbes.interpreter;

import hobbes.parser.SourceLocation;

public abstract class ExecutionFrame {
	
	private Scope scope;
	
	public ExecutionFrame(Scope s) {
		scope = s;
	}
	
	public Scope getScope() {
		return scope;
	}
	
	public abstract String show();
	
	protected String showLoc(SourceLocation loc) {
		return (loc == null ? "    [internal]" : loc.show());
	}
	
}
