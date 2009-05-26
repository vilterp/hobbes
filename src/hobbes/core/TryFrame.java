package hobbes.core;

import hobbes.ast.TryNode;

public class TryFrame extends ExecutionFrame {

	private TryNode tryNode;
	
	public TryFrame(ExecutionFrame e, ObjectSpace o, TryNode t) {
		super(e,o);
		tryNode = t;
	}
	
}
