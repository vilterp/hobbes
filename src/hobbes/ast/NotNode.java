package hobbes.ast;

import hobbes.parser.SourceLine;
import hobbes.parser.Token;

public class NotNode implements ExpressionNode {
	
	private Token origin;
	private ExpressionNode expression;
	
	public NotNode(Token o, ExpressionNode expr) {
		origin = o;
		expression = expr;
	}
	
	public String toString() {
		return "not(" + expression + ")";
	}
	
	public SourceLine getLine() {
		if(origin != null)
			return origin.getLine();
		else
			return null;
	}
	
	public ExpressionNode getExpr() {
		return expression;
	}
	
}
