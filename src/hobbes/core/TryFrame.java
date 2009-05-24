package hobbes.core;

import hobbes.ast.TryNode;

public class TryFrame extends ExecutionFrame {

	private TryNode tryNode;
	
	public TryFrame(ExecutionFrame e, TryNode t) {
		super(e);
		tryNode = t;
	}
	
}
