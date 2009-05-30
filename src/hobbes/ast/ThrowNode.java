package hobbes.ast;

import hobbes.parser.Token;

public class ThrowNode implements StatementNode {
	
	private Token origin;
	private StringNode errorName;
	private ExpressionNode errorDesc;
	
	public ThrowNode(Token o, StringNode n, ExpressionNode d) {
		origin = o;
		errorName = n;
		errorDesc = d;
	}
	
	public ThrowNode(Token o, StringNode n) {
		origin = o;
		errorName = n;
		errorDesc = null;
	}
	
	public String toString() {
		return "raise(" + errorName
				+ (errorDesc == null ? "" : "," + errorDesc)
				+ ")";
	}
	
	public StringNode getName() {
		return errorName;
	}
	
	public ExpressionNode getDesc() {
		return errorDesc;
	}
	
	public Token getOrigin() {
		return origin;
	}
	
}
