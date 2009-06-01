package hobbes.ast;

import hobbes.parser.Token;

public class BreakNode implements StatementNode {
	
	Token origin;
	
	public BreakNode(Token o) {
		origin = o;
	}
	
	public Token getOrigin() {
		return origin;
	}
	
}
