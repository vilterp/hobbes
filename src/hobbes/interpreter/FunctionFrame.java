package hobbes.interpreter;

public abstract class FunctionFrame extends ExecutionFrame {

	public FunctionFrame(Scope s) {
		super(s);
	}
	
	public abstract String getName();

}
