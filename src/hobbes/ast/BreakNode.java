package hobbes.ast;

import hobbes.parser.Token;

public class BreakNode implements StatementNode {
	
	private Token origin;
	
	public BreakNode(Token o) {
		origin = o;
	}
	
	public String toString() {
		return "break";
	}
	
	public Token getOrigin() {
		return origin;
	}
	
}
