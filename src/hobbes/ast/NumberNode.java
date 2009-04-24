package hobbes.ast;

import hobbes.parser.Token;

public class NumberNode implements SyntaxNode {
	
	public Token origin;
	// TODO: capture line? for runtime errors... variables too...
	
	public NumberNode(Token token) {
		origin = token;
	}
	
	public String toString() {
		return origin.getValue();
	}
	
}
