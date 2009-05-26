package hobbes.core;

public abstract class ExecutionFrame {
	
	private ExecutionFrame enclosing;
	private Scope scope;
	
	public ExecutionFrame(ExecutionFrame e, ObjectSpace o) {
		enclosing = e;
		scope = new Scope(o);
	}
	
	public ExecutionFrame getEnclosing() {
		return enclosing;
	}
	
	public Scope getScope() {
		return scope;
	}
	
}
