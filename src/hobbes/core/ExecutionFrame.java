package hobbes.core;

public abstract class ExecutionFrame {
	
	protected ExecutionFrame enclosing;
	
	public ExecutionFrame(ExecutionFrame e) {
		enclosing = e;
	}
	
	public ExecutionFrame getEnclosing() {
		return enclosing;
	}
	
}
