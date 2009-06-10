package hobbes.ast;

import hobbes.parser.SourceLine;
import hobbes.parser.Token;

public class NegativeNode implements ExpressionNode {
	
	private Token origin;
	private ExpressionNode expression;
	
	public NegativeNode(Token o, ExpressionNode expr) {
		origin = o;
		expression = expr;
	}
	
	public String toString() {
		return "-(" + expression + ")";
	}
	
	public SourceLine getLine() {
		return origin.getLine();
	}
	
	public ExpressionNode getExpr() {
		return expression;
	}
	
	public Token getOrigin() {
		return origin;
	}
	
}
