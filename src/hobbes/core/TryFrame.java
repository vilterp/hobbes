package hobbes.core;

import hobbes.ast.TryNode;

public class TryFrame extends ExecutionFrame {

	private TryNode tryNode;
	
	public TryFrame(Scope s, TryNode t) {
		super(s);
		tryNode = t;
	}
	
}
