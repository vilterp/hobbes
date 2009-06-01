package hobbes.ast;

public class ThrowNode implements StatementNode {
	
	private ExpressionNode exception;
	
	public ThrowNode(ExpressionNode e) {
		exception = e;
	}
	
	public String toString() {
		return "raise(" + exception + ")";
	}
	
}
