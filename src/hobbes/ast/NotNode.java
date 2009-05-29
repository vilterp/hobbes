package hobbes.ast;

public class NotNode implements ExpressionNode {
	
	private ExpressionNode expression;
	
	public NotNode(ExpressionNode expr) {
		expression = expr;
	}
	
	public String toString() {
		return "not(" + expression + ")";
	}
	
	public ExpressionNode getExpr() {
		return expression;
	}
	
}
