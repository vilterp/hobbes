package hobbes.ast;

import hobbes.parser.Token;

public class ReturnNode implements StatementNode {
	
	private Token origin;
	private ExpressionNode expr;
	
	public ReturnNode(ExpressionNode e) {
		expr = e;
	}
	
	public String toString() {
		return "return(" + expr + ")";
	}
	
	public Token getOrigin() {
		return origin;
	}
	
	public ExpressionNode getExpr() {
		return expr;
	}
	
}
