package hobbes.ast;

public class RaiseNode implements StatementNode {
	
	private ExpressionNode exception;
	
	public RaiseNode(ExpressionNode e) {
		exception = e;
	}
	
	public String toString() {
		return "raise(" + exception + ")";
	}
	
}
