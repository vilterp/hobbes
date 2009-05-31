package hobbes.ast;

public class NegativeNode implements ExpressionNode {
	
	private ExpressionNode expression;
	
	public NegativeNode(ExpressionNode expr) {
		expression = expr;
	}
	
	public String toString() {
		return "-(" + expression + ")";
	}
	
}
