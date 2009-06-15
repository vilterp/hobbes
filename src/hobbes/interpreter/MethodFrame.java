package hobbes.interpreter;

public abstract class MethodFrame extends ExecutionFrame {

	public MethodFrame(Scope s) {
		super(s);
	}
	
	public abstract String getName();

}
