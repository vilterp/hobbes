package hobbes.ast;

import hobbes.parser.Token;

public class ContinueNode implements StatementNode {
	
	private Token origin;
	
	public ContinueNode(Token o) {
		origin = o;
	}
	
	public String toString() {
		return "continue";
	}
	
	public Token getOrigin() {
		return origin;
	}
	
}
