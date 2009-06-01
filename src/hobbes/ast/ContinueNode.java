package hobbes.ast;

import hobbes.parser.Token;

public class ContinueNode implements StatementNode {
	
	Token origin;
	
	public ContinueNode(Token o) {
		origin = o;
	}
	
	public Token getOrigin() {
		return origin;
	}
	
}