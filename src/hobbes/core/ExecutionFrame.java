package hobbes.core;

public abstract class ExecutionFrame {
	
	private Scope scope;
	
	public ExecutionFrame(Scope s) {
		scope = s;
	}
	
	public Scope getScope() {
		return scope;
	}
	
}
